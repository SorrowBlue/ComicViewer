import ComposeApp
import Foundation
import SwiftZip

@testable import AMSMB2

class IosSmbFileClientImpl: IosSmbFileClient {

    class Factory: IosSmbFileClientFactory {
        func create(bookshelf: SmbServer) -> any IosSmbFileClient {
            return IosSmbFileClientImpl(bookshelf: bookshelf)
        }
    }

    let bookshelf: SmbServer
    let serverURL: URL
    let domain: String
    let credential: URLCredential?

    init(bookshelf: SmbServer) {
        self.bookshelf = bookshelf
        self.serverURL = URL(
            string: "smb://"
                + bookshelf.host.precomposedStringWithCanonicalMapping
        )!
        self.credential =
            if let auth = bookshelf.auth as? SmbServerAuthUsernamePassword {
                URLCredential(
                    user: auth.username,
                    password: auth.password,
                    persistence: URLCredential.Persistence.forSession
                )
            } else {
                nil
            }
        self.domain =
            if let auth = bookshelf.auth as? SmbServerAuthUsernamePassword {
                auth.domain
            } else {
                ""
            }

    }

    lazy private var manager = SMB2Manager(
        url: self.serverURL,
        domain: self.domain,
        credential: self.credential
    )!

    private func currentShare() -> String? {
        return self.manager.client?.share
    }

    private func connectShare(path: String) async throws -> SMB2Manager {
        let share = path.asShareName()
        if share == currentShare() {
            return self.manager
        }
        try await self.manager.connectShare(name: share)
        return self.manager
    }

    func connect(path: String) async throws {
        let client = try await connectShare(path: path)
        let subPath = path.withoutShare()
        try await client.attributesOfItem(atPath: subPath)
    }

    func current(path: String) async throws -> IosSmbFile {
        let client = try await connectShare(path: path)
        let subPath = path.withoutShare()
        let attributes = try await client.attributesOfItem(atPath: subPath)
        let file = IosSmbFile(
            path: path.nfcString(),
            name: attributes.name?.nfcString() ?? "",
            isDirectory: attributes.isDirectory,
            fileSize: attributes.fileSize ?? 0,
            contentModificationDate: attributes.contentModificationDate?.toInt64() ?? 0
        )
        return file
    }

    func listDirectory(path: String) async throws -> [IosSmbFile] {
        let url = URL(fileURLWithPath: path)
        let subPath = path.withoutShare()
        let client = try await connectShare(path: path)
        let files = try await client.contentsOfDirectory(atPath: subPath)
        let list = files.map { file in
            let path =
                url.path(percentEncoded: false) + file.name!
                + (file.isDirectory ? "/" : "")
            let name = file.name!
            return IosSmbFile(
                path: path.precomposedStringWithCanonicalMapping,
                name: name,
                isDirectory: file.isDirectory,
                fileSize: file.fileSize ?? 0,
                contentModificationDate: file.contentModificationDate?.toInt64()
                    ?? 0
            )
        }
        return list
    }

    func bufferedSource(file: any File) async throws -> any OkioBufferedSource {
        let client = try await connectShare(path: file.path)
        let subPath = file.path.withoutShare()
        let data = try await client.contents(atPath: subPath)
        return IosSmbFileClientKt.toBufferedSource(data)
    }

    func attribute(path: String) async throws -> FileAttribute {

        let client = try await connectShare(path: path)
        let subPath = path.withoutShare()
        let attributes = try await client.attributesOfItem(atPath: subPath)
        return FileAttribute.init(
            archive: false,
            compressed: false,
            directory: attributes.isDirectory,
            normal: false,
            readonly: false,
            system: false,
            temporary: false,
            sharedRead: false,
            hidden: false,
            volume: false
        )
    }

    func seekableInputStream(file: any File) async throws
        -> any ClientSeekableInputStream
    {
        return try SmbSeekableInputStream(client: self.manager, path: file.path)
    }

}

extension String {
    func asShareName() -> String {
        let url = URL(fileURLWithPath: self)
        let share = url.pathComponents[1]
        return share.precomposedStringWithCanonicalMapping
    }

    func withoutShare() -> String {
        let url = URL(fileURLWithPath: self)
        let subPath =
            "/" + url.pathComponents.dropFirst(2).joined(separator: "/")
        return subPath.precomposedStringWithCanonicalMapping
    }
    
    func nfcString() -> String {
        return self.precomposedStringWithCanonicalMapping
    }
}

extension Date {
    func toInt64() -> Int64 {
        return Int64(timeIntervalSince1970 * 1000)
    }
}
