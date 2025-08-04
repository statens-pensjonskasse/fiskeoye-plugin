package no.spk.fiskeoye.plugin.listeners.window

import com.intellij.openapi.project.Project
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import no.spk.fiskeoye.plugin.enum.ContentType
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.CONTENT_TYPE_KEY
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.FILENAME_COUNTER_KEY
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.FILE_CONTENT_COUNTER_KEY

internal class TabListener(val project: Project) : ContentManagerListener {
    override fun contentRemoved(event: ContentManagerEvent) {
        when (event.content.getUserData(CONTENT_TYPE_KEY)) {
            ContentType.FILE_CONTENT -> {
                val currentCount = project.getUserData(FILE_CONTENT_COUNTER_KEY) ?: 1
                if (currentCount > 1) {
                    project.putUserData(FILE_CONTENT_COUNTER_KEY, currentCount - 1)
                }
            }

            ContentType.FILENAME -> {
                val currentCount = project.getUserData(FILENAME_COUNTER_KEY) ?: 1
                if (currentCount > 1) {
                    project.putUserData(FILENAME_COUNTER_KEY, currentCount - 1)
                }
            }

            else -> {}
        }
    }
}