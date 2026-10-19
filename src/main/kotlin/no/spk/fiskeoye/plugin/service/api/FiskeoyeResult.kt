package no.spk.fiskeoye.plugin.service.api

data class FiskeoyeResult(
    val url: String,
    val hits: List<FiskeoyeHit>?,
    val message: String
)
