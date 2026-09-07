import ComposeApp
import Foundation
import SwiftZip

class IosZipFileReaderImpl: IosZipFileReader {

    class Factory: IosZipFileReaderFactory {
        func create(
            seekableInputStream: any SeekableInputStream,
            supportedExtension: Set<String>
        ) throws -> any IosZipFileReader {
            return try IosZipFileReaderImpl(
                seekableInputStream: seekableInputStream,
                supportedExtension: supportedExtension
            )
        }
    }

    private let adapter: SmbZipSourceSeekable
    private let supportedExtension: Set<String>
    private let archive: ZipArchive
    private let entries: [ZipEntry]

    private init(
        seekableInputStream: any SeekableInputStream,
        supportedExtension: Set<String>
    ) throws {
        guard let iosSeekable = seekableInputStream as? any IosSeekableInputStream else {
            throw NSError(
                domain: "IosZipFileReader",
                code: -1,
                userInfo: [NSLocalizedDescriptionKey: "seekableInputStream must conform to IosSeekableInputStream"]
            )
        }
        self.adapter = try SmbZipSourceSeekable(seekable: iosSeekable)
        self.supportedExtension = supportedExtension
        let source = try ZipSource.init(adapter: adapter)
        self.archive = try ZipArchive(source: source)
        self.entries = try self.archive.entries().filter({ zipEntry in
            return try !zipEntry.getName().hasSuffix("/")
        })

    }

    func close() {
        do {
            try self.adapter.close()
        } catch {

        }
    }

    func source(pageIndex: Int32) async throws -> any Kotlinx_io_coreSource {
        let data = try self.entries[Int(pageIndex)].data()
        return IosSmbFileClientKt.toSource(data)
    }

    func fileName(pageIndex: Int32) async throws -> String {
        return try self.entries[Int(pageIndex)].getName()
    }

    func fileSize(pageIndex: Int32) async throws -> KotlinLong {
        return 0
    }

    func pageCount() async throws -> KotlinInt {
        return KotlinInt(integerLiteral: self.entries.count)
    }
}

extension Int64 {
    func tokotlinLong() -> KotlinLong {
        return KotlinLong(value: self)
    }
}
