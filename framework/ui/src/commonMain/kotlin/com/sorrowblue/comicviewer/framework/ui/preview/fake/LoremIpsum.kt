package com.sorrowblue.comicviewer.framework.ui.preview.fake

val LoremIpsum = listOf(
    "Lorem ipsum dolor",
    "Vivamus vitae sapien sodales",
    "Pellentesque non justo faucibus",
    "Donec ut eros",
    "Donec venenatis nisl eget sapien lacinia",
    "Fusce laoreet sapien vel nisi porta",
    "Nullam vestibulum",
    "Curabitur mollis eros",
    "Nam quis eros quis risus",
    "Nulla vehicula leo vel faucibus egestas",
    "Vivamus vel mauris dignissim",
    "Maecenas semper risus at imperdiet auctor",
    "Praesent",
    "Pellentesque dignissim",
    "Suspendisse",
    "Nam sit amet",
    "Suspendisse",
    "Vivamus pretium dui",
    "Suspendisse eu ante",
    "Nam ac nisl ac tellus pellentesque",
)

fun nextLoremIpsum() = LoremIpsum[loremIpsumIndex++].also {
    if (loremIpsumIndex >= LoremIpsum.size) loremIpsumIndex = 0
}

private var loremIpsumIndex = 0
