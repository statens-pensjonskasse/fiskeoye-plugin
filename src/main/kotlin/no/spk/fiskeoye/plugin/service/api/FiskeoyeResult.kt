package no.spk.fiskeoye.plugin.service.api

import org.jsoup.nodes.Element

data class FiskeoyeResult(
    val url: String,
    val elements: List<Element>?,
    val message: String
)
