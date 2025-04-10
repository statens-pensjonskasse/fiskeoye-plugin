package no.spk.fiskeoye.plugin

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.ui.FilenamePanel

internal class FiskeoyeToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory: ContentFactory = ContentFactory.getInstance()

        val fileContentPanel = FileContentPanel()
        val fileContent = contentFactory.createContent(fileContentPanel, "File Content", false)
        toolWindow.contentManager.addContent(fileContent)

        val filenamePanel = FilenamePanel()
        val filename = contentFactory.createContent(filenamePanel, "Filename", false)
        toolWindow.contentManager.addContent(filename)
    }

}
