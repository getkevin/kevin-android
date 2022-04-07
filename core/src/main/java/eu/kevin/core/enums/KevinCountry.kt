package eu.kevin.core.enums

enum class KevinCountry(val iso: String) {
    AUSTRIA("at"),
    BELGIUM("be"),
    BULGARIA("bg"),
    CYPRUS("cy"),
    CZECHIA("cz"),
    GERMANY("de"),
    DENMARK("dk"),
    ESTONIA("ee"),
    SPAIN("es"),
    FINLAND("fi"),
    FRANCE("fr"),
    UNITED_KINGDOM("gb"),
    CROATIA("hr"),
    HUNGARY("hu"),
    IRELAND("ie"),
    ITALY("it"),
    LITHUANIA("lt"),
    LUXEMBOURG("lu"),
    LATVIA("lv"),
    MALTA("mt"),
    NETHERLANDS("nl"),
    NORWAY("no"),
    POLAND("pl"),
    PORTUGAL("pt"),
    ROMANIA("ro"),
    SWEDEN("se"),
    SLOVENIA("si"),
    SLOVAKIA("sk");

    companion object {
        fun parse(iso: String?): KevinCountry? {
            return values().firstOrNull { it.iso.equals(iso, ignoreCase = true) }
        }
    }
}