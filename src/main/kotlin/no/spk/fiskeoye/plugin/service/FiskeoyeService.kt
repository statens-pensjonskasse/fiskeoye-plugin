package no.spk.fiskeoye.plugin.service

import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.intellij.openapi.diagnostic.Logger
import java.time.Duration
import no.spk.fiskeoye.plugin.service.api.FileContentRequest
import no.spk.fiskeoye.plugin.service.api.FilenameRequest
import no.spk.fiskeoye.plugin.service.api.FiskeoyeHit
import no.spk.fiskeoye.plugin.service.api.FiskeoyeRequest
import no.spk.fiskeoye.plugin.service.api.FiskeoyeResult
import no.spk.fiskeoye.plugin.util.getGeneralErrorMessage
import org.http4k.client.JavaHttpClient
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.lens.basicAuthentication

internal object FiskeoyeService {

    private val logger: Logger = Logger.getInstance(FiskeoyeService::class.java)
    private val mapper = jsonMapper { addModule(kotlinModule()) }
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
            send(request)
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
            send(request)
        }
    }

    private fun generateCacheKey(request: FiskeoyeRequest, type: String): String = "${type}_${request.getUrl().hashCode()}"

    private fun send(fiskeoyeRequest: FiskeoyeRequest): FiskeoyeResult {
        val url = fiskeoyeRequest.getUrl()
        logger.info("Request: $url")
        val hits: List<FiskeoyeHit>
        try {
            val request = Request(Method.GET, url)
                .header("Accept", "application/json")
                .basicAuthentication(Credentials("fiskeoye-plugin", ""))
            val response = JavaHttpClient().invoke(request)
            if (response.status != Status.OK) {
                val message = "Ops! Request to fiskeoye is failing with http_status : ${response.status}"
                logger.warn(message)
                return FiskeoyeResult(url, null, message)
            }
            hits = mapper.readValue<List<FiskeoyeHit>>(response.bodyString())
            logger.info("Response received!")
        } catch (ex: IllegalArgumentException) {
            logger.warn(ex.message, ex)
            return FiskeoyeResult(url, null, "Ops! Baseurl is not defined or wrong. Please update using: Setting > Tools > Fiskeoye > Base-url")
        } catch (ex: Exception) {
            logger.warn(ex.message, ex)
            return FiskeoyeResult(url, null, getGeneralErrorMessage())
        }
        return FiskeoyeResult(url, hits, "")
    }

}