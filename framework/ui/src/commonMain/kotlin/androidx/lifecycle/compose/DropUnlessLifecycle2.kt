package androidx.lifecycle.compose

import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner

@CheckResult
@Composable
fun <T> dropUnlessResumed(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: (T) -> Unit,
): (T) -> Unit = dropUnlessStateIsAtLeast(State.RESUMED, lifecycleOwner, block)

@CheckResult
@Composable
fun <T, V> dropUnlessResumed(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: (T, V) -> Unit,
): (T, V) -> Unit = dropUnlessStateIsAtLeast(State.RESUMED, lifecycleOwner, block)

@CheckResult
@Composable
private fun <T> dropUnlessStateIsAtLeast(
    state: State,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: (T) -> Unit,
): (T) -> Unit {
    require(state != State.DESTROYED) {
        "Target state is not allowed to be `Lifecycle.State.DESTROYED` because Compose disposes " +
            "of the composition before `Lifecycle.Event.ON_DESTROY` observers are invoked."
    }

    return { value ->
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(state)) {
            block(value)
        }
    }
}

@CheckResult
@Composable
private fun <T, V> dropUnlessStateIsAtLeast(
    state: State,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: (T, V) -> Unit,
): (T, V) -> Unit {
    require(state != State.DESTROYED) {
        "Target state is not allowed to be `Lifecycle.State.DESTROYED` because Compose disposes " +
            "of the composition before `Lifecycle.Event.ON_DESTROY` observers are invoked."
    }

    return { value1, value2 ->
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(state)) {
            block(value1, value2)
        }
    }
}
