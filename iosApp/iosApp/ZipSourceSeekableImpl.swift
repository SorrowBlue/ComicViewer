import ComposeApp
import SwiftZip

class ZipSourceSeekableImpl : ZipSourceSeekable {
    
    private let seekableInputStream: ClientSeekableInputStream
    
    init(seekableInputStream: ClientSeekableInputStream) {
        self.seekableInputStream = seekableInputStream
    }
    
    func seek(offset: Int, relativeTo whence: SwiftZip.ZipWhence) throws {
        seekableInputStream.seek(offset: Int64(offset), whence: whence.rawValue)
    }
    
    func tell() throws -> Int {
        return Int(seekableInputStream.position())
    }
    
    func open() throws {
        seekableInputStream.open()
    }
    
    func read(to buffer: UnsafeMutableRawPointer, count: Int) throws -> Int {
        return Int(seekableInputStream.read(pointer: buffer, count: Int32(count)))
    }
    
    func close() throws {
        seekableInputStream.close()
    }
    
    func stat() throws -> SwiftZip.ZipStat {
        return SwiftZip.ZipStat(size: Int(seekableInputStream.size()))
    }
}
