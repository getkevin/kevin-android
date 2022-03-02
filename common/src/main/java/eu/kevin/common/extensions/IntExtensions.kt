package eu.kevin.common.extensions

fun Int.toHexColor(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}