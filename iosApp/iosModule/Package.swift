// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "iosModule",
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "iosModule",
            targets: ["iosModule"]),
    ],
    targets: [
        .binaryTarget(name: "shared", path: "../../data/reader/zip/build/XCFrameworks/release/zipReader.xcframework"),
        .target(
            name: "iosModule", dependencies: ["shared"]),

    ]
)
