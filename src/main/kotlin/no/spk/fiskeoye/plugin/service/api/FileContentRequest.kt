package no.spk.fiskeoye.plugin.service.api

import no.spk.fiskeoye.plugin.enum.SortBy
import no.spk.fiskeoye.plugin.util.toOnOff
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets.UTF_8

internal data class FileContentRequest(
    val includeText: String,
    val isExclude: Boolean,
    val excludeText: String,
    val isCaseSensitive: Boolean,
    val sortBy: SortBy
) : FiskeoyeRequest() {

    override fun getUrl(): String {
        return "$fiskeoyeUrl/index.php?searchstring=${encode(includeText, UTF_8)}&" +
                "negate=${isExclude.toOnOff()}&" +
                "filterstring=${encode(excludeText, UTF_8)}&" +
                "cs=${isCaseSensitive.toOnOff()}&" +
                "sorting=${sortBy.value.lowercase()}"
    }

}
