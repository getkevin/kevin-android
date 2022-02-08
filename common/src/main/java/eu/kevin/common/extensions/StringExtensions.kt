package eu.kevin.common.extensions

fun String.removeWhiteSpaces(): String {
    return this.replace(" ", "")
}