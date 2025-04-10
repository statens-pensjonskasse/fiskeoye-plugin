package no.spk.fiskeoye.plugin.service

import com.intellij.openapi.diagnostic.Logger
import no.spk.fiskeoye.plugin.enum.SortBy
import no.spk.fiskeoye.plugin.service.api.FileContentRequest
import no.spk.fiskeoye.plugin.service.api.FilenameRequest
import no.spk.fiskeoye.plugin.service.api.FiskeoyeRequest
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

internal object FiskeoyeService {

    private val logger: Logger = Logger.getInstance(FiskeoyeService::class.java)
    private const val ID = "resultat_linje"

    internal fun getFileContent(
        includeText: String,
        isExclude: Boolean,
        excludeText: String,
        isCaseSensitive: Boolean,
        sortBy: SortBy
    ): Pair<String, List<Element>?> {
        val request = FileContentRequest(includeText, isExclude, excludeText, isCaseSensitive, sortBy)
        return send(request) { it.hasAttr("class") && ID == it.className() && it.children().isNotEmpty() }
    }

    internal fun getFilename(
        includeText: String,
        isCaseSensitive: Boolean,
        isSearchInFullPath: Boolean,
        sortBy: SortBy
    ): Pair<String, List<Element>?> {
        val request = FilenameRequest(includeText, isCaseSensitive, isSearchInFullPath, sortBy)
        return send(request) { it.hasAttr("href") && it.hasParent() && ID == it.parent()!!.className() }
    }

    private fun send(fiskeoyeRequest: FiskeoyeRequest, filterPredicate: (Element) -> Boolean): Pair<String, List<Element>?> {
        val url = fiskeoyeRequest.getUrl()
        logger.info("Request: $url")
        val elements: List<Element>
        try {
            val request = Request(Method.GET, url)
            val response = JavaHttpClient().invoke(request)
            if (response.status != Status.OK) {
                logger.warn("Fiskeoye kall feiler med status : ${response.status}")
                return Pair(url, null)
            }
            elements = Jsoup.parse(response.body.toString()).allElements.filter(filterPredicate)
            logger.info("Response received!")
        } catch (ex: Exception) {
            logger.warn(ex.message, ex)
            return Pair(url, null)
        }
        return Pair(url, elements)
    }

}