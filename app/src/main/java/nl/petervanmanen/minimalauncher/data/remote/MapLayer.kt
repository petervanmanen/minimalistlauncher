package nl.petervanmanen.minimalauncher.data.remote

/**
 * OSM-based raster tile styles, all free and requiring no API key — matching
 * the zero-config approach used elsewhere. Each still needs a descriptive
 * User-Agent per its usage policy, same as the default OpenStreetMap tiles.
 */
enum class MapLayer(val label: String, private val urlTemplate: String) {
    STANDARD("Standard", "https://tile.openstreetmap.org/{z}/{x}/{y}.png"),
    HUMANITARIAN("Humanitarian", "https://tile-a.openstreetmap.fr/hot/{z}/{x}/{y}.png"),
    CYCLOSM("Cycle", "https://a.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png"),
    TOPO("Topo", "https://a.tile.opentopomap.org/{z}/{x}/{y}.png"),
    ;

    fun tileUrl(z: Int, x: Int, y: Int): String = urlTemplate
        .replace("{z}", z.toString())
        .replace("{x}", x.toString())
        .replace("{y}", y.toString())

    companion object {
        fun fromId(id: String?): MapLayer = entries.find { it.name == id } ?: STANDARD
    }
}
