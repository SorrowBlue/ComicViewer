import AMSMB2
import ComposeApp
import Foundation
import SwiftZip

class SmbZipSourceSeekable : ZipSourceSeekable {
    
    let seekable: ClientSeekableInputStream
    
    init(seekable: ClientSeekableInputStream) throws {
        self.seekable = seekable
    }
    
    func open() throws {
        self.seekable.open()
    }
    
    func read(to buffer: UnsafeMutableRawPointer, count: Int) throws -> Int {
        return Int(self.seekable.read(pointer: buffer, count: Int32(count)))
    }
    
    func close() throws {
        self.seekable.close()
    }
    
    func stat() throws -> ZipStat {
        return ZipStat(size: Int(self.seekable.size()))
    }
    
    func seek(offset: Int, relativeTo whence: ZipWhence) throws {
        self.seekable.seek(offset: Int64(offset), whence: whence.rawValue)
    }
    
    func tell() throws -> Int {
        return Int(self.seekable.position())
    }
}

extension ZipWhence {
    
    func asSeekWhence() -> SMB2FileHandle.SeekWhence {
        return switch self {
        case .origin: SMB2FileHandle.SeekWhence.set
        case .end: SMB2FileHandle.SeekWhence.end
        case .currentPosition: SMB2FileHandle.SeekWhence.current
        default: SMB2FileHandle.SeekWhence.set
        }
    }
}
