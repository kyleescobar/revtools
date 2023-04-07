package io.github.kyleescobar.revtools.logger

enum class ConvertColor(val ansiColor: String, private val code: String) {
    RESET("\u001B[0m", "r"), BLACK("\u001B[30m", "0"), DARK_BLUE("\u001B[34m", "1"), DARK_GREEN(
        "\u001B[32m",
        "2"
    ),
    DARK_CYAN("\u001B[36m", "3"), DARK_RED("\u001B[31m", "4"), DARK_PURPLE("\u001B[35m", "5"), DARK_YELLOW(
        "\u001B[33m",
        "6"
    ),
    LIGHT_GRAY("\u001B[97m", "7"), DARK_GRAY("\u001B[90m", "8"), LIGHT_BLUE(
        "\u001B[94m",
        "9"
    ),
    LIGHT_GREEN("\u001B[92m", "a"), LIGHT_CYAN("\u001B[96m", "b"), LIGHT_RED(
        "\u001B[91m",
        "c"
    ),
    LIGHT_PURPLE("\u001B[95m", "d"), LIGHT_YELLOW("\u001B[93m", "e"), WHITE("\u001B[37m", "f");

    val withAmp: String
        get() = "&$code"
    val withParagraph: String
        get() = "§$code"

    companion object {
        private fun replaceColor(color: ConvertColor, message: String, replacement: String): String {
            var message = message
            message =
                if (message.contains(color.withAmp)) message.replace(color.withAmp.toRegex(), replacement) else message
            return if (message.contains(color.withParagraph)) message.replace(
                color.withParagraph.toRegex(),
                replacement
            ) else message
        }

        fun parseColor(message: String): String {
            var message = message
            for (convertColor in values()) {
                message = replaceColor(convertColor, message, convertColor.ansiColor)
            }
            return message
        }
    }
}