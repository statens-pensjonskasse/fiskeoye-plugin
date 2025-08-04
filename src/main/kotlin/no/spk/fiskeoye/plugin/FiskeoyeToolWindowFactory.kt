package no.spk.fiskeoye.plugin

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory
import no.spk.fiskeoye.plugin.actions.window.AddFileContentTabAction
import no.spk.fiskeoye.plugin.actions.window.AddFilenameTabAction
import no.spk.fiskeoye.plugin.enum.ContentType
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Add
import no.spk.fiskeoye.plugin.listeners.window.TabListener
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.ui.FilenamePanel
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.FILENAME_COUNTER_KEY
import no.spk.fiskeoye.plugin.util.FiskeoyeKeys.FILE_CONTENT_COUNTER_KEY
import no.spk.fiskeoye.plugin.util.createContent

internal class FiskeoyeToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory: ContentFactory = ContentFactory.getInstance()
        val toolWindowEx = toolWindow as ToolWindowEx

        project.putUserData(FILE_CONTENT_COUNTER_KEY, 1)
        project.putUserData(FILENAME_COUNTER_KEY, 1)

        val fileContent = contentFactory.createContent(FileContentPanel(), "File Content", ContentType.FILE_CONTENT)
        toolWindowEx.contentManager.addContent(fileContent)

        val filename = contentFactory.createContent(FilenamePanel(), "File Name", ContentType.FILENAME)
        toolWindowEx.contentManager.addContent(filename)

        toolWindowEx.contentManager.addContentManagerListener(TabListener(project))
        toolWindowEx.setTabActions(buildTabActionGroup(toolWindow, contentFactory))
    }

    private fun buildTabActionGroup(toolWindow: ToolWindow, contentFactory: ContentFactory): DefaultActionGroup {
        return DefaultActionGroup("New Fiskeoye Tab", true).apply {
            templatePresentation.icon = Add
            add(AddFileContentTabAction(toolWindow, contentFactory))
            add(AddFilenameTabAction(toolWindow, contentFactory))
        }
    }

}