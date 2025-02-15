import UIKit
import SwiftUI
import ComposeApp
import zipReader
import ZIPFoundation

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}

class ZipReader23 : ZipReader2 {
    
    func input(path: String) throws {
        let string = "Some string!"
        guard let url = URL(string: path) else {
            return
        }
        let archive = try Archive(url: url, accessMode: .read)
        var currentIndex = 0
        var targetEntity : Entry? = nil
        for entry in archive {
            if currentIndex == 0 {
                targetEntity = entry
            }
        }
        guard let imageEntity = targetEntity else {
            return
        }
        let data = try archive.extract(imageEntity, consumer: { (data) in
//            data.copyBytes(to: <#T##UnsafeMutableBufferPointer<DestinationType>#>)
            print(data.count)
        })
        guard let data = string.data(using: .utf8) else { return }
        try archive.addEntry(with: "inMemory.txt", type: .file, uncompressedSize: Int64(data.count), bufferSize: 4, provider: { (position, size) -> Data in
            return data.subdata(in: Data.Index(position)..<Int(position)+size)
        })
        let archiveData = archive.data
    }
    
}
