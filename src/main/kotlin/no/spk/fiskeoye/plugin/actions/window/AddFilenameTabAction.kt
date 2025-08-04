package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.enum.ContentType
import no.spk.fiskeoye.plugin.ui.FilenamePanel
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.FILENAME_COUNTER_KEY
import no.spk.fiskeoye.plugin.util.createContent

internal class AddFilenameTabAction(val toolWindow: ToolWindow, val contentFactory: ContentFactory) : FiskeoyeAction("File Name"){

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val currentCount = project.getUserData(FILENAME_COUNTER_KEY) ?: 1
        val newCount = currentCount + 1
        project.putUserData(FILENAME_COUNTER_KEY, newCount)

        val newFilename = contentFactory.createContent(
            fiskeoyePanel = FilenamePanel(),
            title = "File Name ($newCount)",
            contentType = ContentType.FILENAME,
            closeable = true
        )
        toolWindow.contentManager.addContent(newFilename)
        toolWindow.contentManager.setSelectedContent(newFilename, true)
    }

}