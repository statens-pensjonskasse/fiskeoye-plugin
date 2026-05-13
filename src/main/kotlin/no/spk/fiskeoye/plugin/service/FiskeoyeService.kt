package no.spk.fiskeoye.plugin.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.intellij.openapi.diagnostic.Logger
import java.time.Duration
import no.spk.fiskeoye.plugin.service.api.FileContentRequest
import no.spk.fiskeoye.plugin.service.api.FilenameRequest
import no.spk.fiskeoye.plugin.service.api.FiskeoyeRequest
import org.http4k.client.JavaHttpClient
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.lens.basicAuthentication
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

internal object FiskeoyeService {

    private val logger: Logger = Logger.getInstance(FiskeoyeService::class.java)
    private const val ID = "resultat_linje"
    private val cache: Cache<String, Pair<String, List<Element>?>> = Caffeine.newBuilder()
        .maximumSize(300)
        .expireAfterWrite(Duration.ofMinutes(55))
        .expireAfterAccess(Duration.ofMinutes(60))
        .recordStats()
        .build()

    internal fun getFileContent(
        includeText: String,
        isExclude: Boolean,
        excludeText: String,
        isCaseSensitive: Boolean
    ): Pair<String, List<Element>?> {
        val request = FileContentRequest(includeText, isExclude, excludeText, isCaseSensitive)
        val cacheKey = generateCacheKey(request, "file_content")

        return cache.get(cacheKey) {
            send(request) { it.hasAttr("class") && ID == it.className() && it.children().isNotEmpty() }
        }
    }

    internal fun getFilename(
        includeText: String,
        isCaseSensitive: Boolean,
        isSearchInFullPath: Boolean
    ): Pair<String, List<Element>?> {
        val request = FilenameRequest(includeText, isCaseSensitive, isSearchInFullPath)
        val cacheKey = generateCacheKey(request, "filename")

        return cache.get(cacheKey) {
            send(request) { it.hasAttr("href") && it.hasParent() && ID == it.parent()!!.className() }
        }
    }

    private fun generateCacheKey(request: FiskeoyeRequest, type: String): String = "${type}_${request.getUrl().hashCode()}"

    private fun send(fiskeoyeRequest: FiskeoyeRequest, filterPredicate: (Element) -> Boolean): Pair<String, List<Element>?> {
        val url = fiskeoyeRequest.getUrl()
        logger.info("Request: $url")
        val elements: List<Element>
        try {
            val request = Request(Method.GET, url)
                .basicAuthentication(Credentials("fiskeoye-plugin", ""))
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