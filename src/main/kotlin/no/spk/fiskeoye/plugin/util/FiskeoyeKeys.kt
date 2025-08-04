package no.spk.fiskeoye.plugin.util

import com.intellij.openapi.util.Key
import no.spk.fiskeoye.plugin.enum.ContentType

internal object FiskeoyeKeys {
    val CONTENT_TYPE_KEY = Key.create<ContentType>("FISKEOYE_CONTENT_TYPE")
    val FILE_CONTENT_COUNTER_KEY = Key.create<Int>("FISKEOYE_FILE_CONTENT_COUNTER")
    val FILENAME_COUNTER_KEY = Key.create<Int>("FISKEOYE_FILENAME_COUNTER")
}