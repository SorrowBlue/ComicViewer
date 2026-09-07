import AMSMB2
import ComposeApp
import SwiftZip

class SmbSeekableInputStream : IosSeekableInputStream {
    
    private var pos: Int = 0
    private let client: SMB2Manager
    private let file: SMB2FileHandle
    private let fileSize: Int
    
    
    init(client: SMB2Manager, path: String) throws {
        self.client = client
        let subPath = path.withoutShare()
        print("#SmbSeekableInputStream$init path:\(path), subPath:\(subPath)")
        do {
            self.file = try SMB2FileHandle(forReadingAtPath: subPath, on: self.client.client.unwrap())
            self.fileSize = Int(try file.fstat().smb2_size)
        } catch {
            print("#SmbSeekableInputStream$init error:" + error.localizedDescription)
            throw NSError(domain: "FileNotFound", code: 404, userInfo: nil)
        }
    }
    
    func open() {
        self.pos = 0
    }
    
    func position() -> Int64 {
        return Int64(self.pos)
    }
    
    func read(pointer: UnsafeMutableRawPointer, count: Int32) -> Int32 {
        let counter = Int(count)
        do {
            let data = try self.file.read(length: counter)
            // 2. 実際に読み取れたサイズを取得
            let bytesRead = data.count
            // 3. Dataの内容を引数のbufferへコピーする
            // UnsafeMutableRawBufferPointer を使うのが最もシンプルです
            let destBuffer = UnsafeMutableRawBufferPointer(start: pointer, count: counter)
            data.copyBytes(to: destBuffer)
            // 4. 実際に読み取ったバイト数を返す
            pos += bytesRead
            return Int32(bytesRead)
        } catch {
            print("read error return 0")
            return 0
        }
    }
    
    func seek(offset: Int64, whence: Int32) -> Int64 {
        let seekWhence = whence.asSeekWhence()
        do {
            let offsetResult = try self.file.lseek(offset: Int64(offset), whence: seekWhence)
            pos = Int(offsetResult)
            return offsetResult
        } catch {
            return 0
        }
    }
    
    func length() -> Int64 {
        return Int64(fileSize)
    }
    
    func read(buf: KotlinByteArray) -> Int32 {
        return read(buf: buf, offset: 0, length: buf.size)
    }
    
    func read(buf: KotlinByteArray, offset: Int32, length: Int32) -> Int32 {
        guard offset >= 0, length >= 0, offset <= buf.size, length <= buf.size - offset else {
            return 0
        }
        if length == 0 {
            return 0
        }
        do {
            let data = try self.file.read(length: Int(length))
            let bytesRead = data.count
            for (index, byte) in data.enumerated() {
                buf.set(index: offset + Int32(index), value: Int8(bitPattern: byte))
            }
            pos += bytesRead
            return Int32(bytesRead)
        } catch {
            print("read error return 0")
            return 0
        }
    }
    
    func seek(position: Int64) -> Int64 {
        return seek(offset: position, whence: SeekableInputStreamCompanion.shared.SEEK_SET)
    }
    
    func close() {
        self.file.close()
    }
}

extension Int32 {
    
    func asSeekWhence() -> SMB2FileHandle.SeekWhence {
        return SMB2FileHandle.SeekWhence.init(rawValue: self)
    }
}
