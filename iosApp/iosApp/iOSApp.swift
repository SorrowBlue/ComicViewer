import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
    
    init() {
        IosSmbFileClientCompanion.shared.factory = IosSmbFileClientImpl.Factory()
        IosZipFileReaderCompanion.shared.factory = IosZipFileReaderImpl.Factory()
    }
}
