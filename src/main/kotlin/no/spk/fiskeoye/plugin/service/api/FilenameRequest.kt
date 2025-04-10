package no.spk.fiskeoye.plugin.service.api

import no.spk.fiskeoye.plugin.enum.SortBy
import no.spk.fiskeoye.plugin.util.toOnOff
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets.UTF_8

internal data class FilenameRequest(
    val includeText: String,
    val isCaseSensitive: Boolean,
    val isSearchInFullPath: Boolean,
    val sortBy: SortBy
) : FiskeoyeRequest() {

    override fun getUrl(): String {
        return "$fiskeoyeUrl/filenames.php?searchstring=${encode(includeText, UTF_8)}&" +
                "cs=${isCaseSensitive.toOnOff()}&" +
                "ufp=${isSearchInFullPath.toOnOff()}&" +
                "sorting=${sortBy.value.lowercase()}"
    }

}
