package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.enum.ContentType
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.FILE_CONTENT_COUNTER_KEY
import no.spk.fiskeoye.plugin.util.createContent

internal class AddFileContentTabAction(val toolWindow: ToolWindow, val contentFactory: ContentFactory) : FiskeoyeAction("File Content") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val currentCount = project.getUserData(FILE_CONTENT_COUNTER_KEY) ?: 1
        val newCount = currentCount + 1
        project.putUserData(FILE_CONTENT_COUNTER_KEY, newCount)

        val newFileContent = contentFactory.createContent(
            fiskeoyePanel = FileContentPanel(),
            title = "File Content ($newCount)",
            contentType = ContentType.FILE_CONTENT,
            closeable = true
        )
        toolWindow.contentManager.addContent(newFileContent)
        toolWindow.contentManager.setSelectedContent(newFileContent, true)
    }

}