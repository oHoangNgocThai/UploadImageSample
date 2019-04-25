package android.thaihn.uploadimagesample.util

enum class FieldType(val key: String, val value: String) {
    TWO_REGIONS_OF_MONEY("2 regions of money", "nenkin_gaku"),
    ALL("All Regions", "all"),
    TWO_REGIONS_OF_MONTH("2 regions of month", "kikan"),
    FOUR_REGIONS_OF_BOTH("4 regions of both month and money", "nenkin_gaku,kikan")
}
