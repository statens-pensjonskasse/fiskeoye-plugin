package no.spk.fiskeoye.plugin.service.api

import no.spk.fiskeoye.plugin.util.getService

abstract class FiskeoyeRequest {

    protected val fiskeoyeUrl: String = getService().baseUrl

    abstract fun getUrl(): String

}
