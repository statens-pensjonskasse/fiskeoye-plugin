package no.spk.fiskeoye.plugin.service.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * A single hit from fiskeoye. [lineno] and [match] are only present for file content searches.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class FiskeoyeHit(
    val url: String,
    val projectkey: String? = null,
    val reponame: String? = null,
    val filename: String,
    val lineno: Int? = null,
    val match: String? = null
)
