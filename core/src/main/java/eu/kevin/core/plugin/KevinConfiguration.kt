package eu.kevin.core.plugin

/**
 * Configuration class for Kevin plugins
 * @property theme resource id of theme to be used in Kevin provided windows
 */
class KevinConfiguration private constructor(
    private val theme: Int
) {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    fun getTheme(): Int {
        return theme
    }

    class Builder {

        private var theme: Int = 0

        /**
         * @param theme theme resource id that will be used in Kevin provided windows
         */
        fun setTheme(theme: Int): Builder {
            this.theme = theme
            return this
        }

        fun build(): KevinConfiguration {
            if (theme < 0) {
                throw KevinException("Invalid theme set!")
            }
            return KevinConfiguration(theme)
        }
    }
}