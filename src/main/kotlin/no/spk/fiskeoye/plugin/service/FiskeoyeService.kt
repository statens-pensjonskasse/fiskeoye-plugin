package no.spk.fiskeoye.plugin.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.intellij.openapi.diagnostic.Logger
import java.time.Duration
import no.spk.fiskeoye.plugin.service.api.FileContentRequest
import no.spk.fiskeoye.plugin.service.api.FilenameRequest
import no.spk.fiskeoye.plugin.service.api.FiskeoyeRequest
import no.spk.fiskeoye.plugin.service.api.FiskeoyeResult
import no.spk.fiskeoye.plugin.util.getGeneralErrorMessage
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
    private val cache: Cache<String, FiskeoyeResult> = Caffeine.newBuilder()
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
    ): FiskeoyeResult {
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
    ): FiskeoyeResult {
        val request = FilenameRequest(includeText, isCaseSensitive, isSearchInFullPath)
        val cacheKey = generateCacheKey(request, "filename")

        return cache.get(cacheKey) {
            send(request) { it.hasAttr("href") && it.hasParent() && ID == it.parent()!!.className() }
        }
    }

    private fun generateCacheKey(request: FiskeoyeRequest, type: String): String = "${type}_${request.getUrl().hashCode()}"

    private fun send(fiskeoyeRequest: FiskeoyeRequest, filterPredicate: (Element) -> Boolean): FiskeoyeResult {
        val url = fiskeoyeRequest.getUrl()
        logger.info("Request: $url")
        val elements: List<Element>
        try {
            val request = Request(Method.GET, url)
                .basicAuthentication(Credentials("fiskeoye-plugin", ""))
            val response = JavaHttpClient().invoke(request)
            if (response.status != Status.OK) {
                val message = "Ops! Request to fiskeoye is failing with http_status : ${response.status}"
                logger.warn(message)
                return FiskeoyeResult(url, null, message)
            }
            elements = Jsoup.parse(response.body.toString()).allElements.filter(filterPredicate)
            logger.info("Response received!")
        } catch (ex: IllegalArgumentException) {
            logger.warn(ex.message, ex)
            return FiskeoyeResult(url, null, "Ops! Baseurl is not defined or wrong. Please update using: Setting > Tools > Fiskeoye > Base-url")
        } catch (ex: Exception) {
            logger.warn(ex.message, ex)
            return FiskeoyeResult(url, null, getGeneralErrorMessage())
        }
        return FiskeoyeResult(url, elements, "")
    }

}