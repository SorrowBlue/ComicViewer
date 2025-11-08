import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.internal.cc.base.logger
import org.gradle.process.ExecOperations

interface GitTagParameters : ValueSourceParameters

abstract class GitTagValueSource @Inject constructor(private val execOperations: ExecOperations) :
    ValueSource<String, GitTagParameters> {
    override fun obtain(): String = runCatching {
        val stdout = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine("git", "describe", "--tags", "--abbrev=1")
            standardOutput = stdout
            isIgnoreExitValue = true
            errorOutput = ByteArrayOutputStream()
        }

        if (result.exitValue == 0) {
            stdout.toString().trim()
        } else {
            logger.error("Warning: Could not get git tag. (Exit code: ${result.exitValue})")
            "UNKNOWN"
        }
    }.onFailure {
        logger.error("Warning: Failed to execute git command: ${it.message}")
    }.getOrDefault("UNKNOWN")
}
