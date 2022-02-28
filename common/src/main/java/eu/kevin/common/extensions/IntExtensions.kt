package eu.kevin.common.extensions

fun Int.toHexColor(): String {
    return java.lang.String.format("#%06X", 0xFFFFFF and this)
}