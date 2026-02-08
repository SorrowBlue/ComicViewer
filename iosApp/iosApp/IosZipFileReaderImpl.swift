import ComposeApp
import Foundation
import SwiftZip

class IosZipFileReaderImpl: IosZipFileReader {

    class Factory: IosZipFileReaderFactory {
        func create(
            seekableInputStream: any ClientSeekableInputStream,
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
        seekableInputStream: ClientSeekableInputStream,
        supportedExtension: Set<String>
    ) throws {
        self.adapter = try SmbZipSourceSeekable(seekable: seekableInputStream)
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

    func doCopyTo(pageIndex: Int32, bufferedSink: any OkioBufferedSink)
        async throws
    {
        let name = try self.entries[Int(pageIndex)].getName()
        let isDirectory = try self.entries[Int(pageIndex)]
            .getExternalAttributes().isDirectory
        print("doCopyTo name: \(name), isDirectory: \(isDirectory)")
        let data = try self.entries[Int(pageIndex)].data()
        IosZipFileReaderKt.writeData(bufferedSink, data: data)
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
