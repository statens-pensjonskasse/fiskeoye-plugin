package no.spk.fiskeoye.plugin

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManagerEvent
import com.intellij.ui.content.ContentManagerListener
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Add
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.ui.FilenamePanel

internal class FiskeoyeToolWindowFactory : ToolWindowFactory, DumbAware {

    private var fileContentCounter = 1
    private var filenameCounter = 1

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory: ContentFactory = ContentFactory.getInstance()
        val toolWindowEx = toolWindow as ToolWindowEx

        val fileContentPanel = FileContentPanel()
        val fileContent = contentFactory.createContent(fileContentPanel, "File Content", false).apply {
            isCloseable = false
            putUserData(CONTENT_TYPE_KEY, ContentType.FILE_CONTENT)
        }
        toolWindowEx.contentManager.addContent(fileContent)

        val filenamePanel = FilenamePanel()
        val filename = contentFactory.createContent(filenamePanel, "File Name", false).apply {
            isCloseable = false
            putUserData(CONTENT_TYPE_KEY, ContentType.FILENAME)
        }
        toolWindowEx.contentManager.addContent(filename)

        toolWindowEx.contentManager.addContentManagerListener(object : ContentManagerListener {
            override fun contentRemoved(event: ContentManagerEvent) {
                val content = event.content
                when (content.getUserData(CONTENT_TYPE_KEY)) {
                    ContentType.FILE_CONTENT -> if (fileContentCounter > 1) fileContentCounter--
                    ContentType.FILENAME -> if (filenameCounter > 1) filenameCounter--
                    else -> {}
                }
            }
        })

        val newTabActionGroup = buildNewTabActionGroup(toolWindow, contentFactory)
        toolWindowEx.setTabActions(newTabActionGroup)
    }

    private fun buildNewTabActionGroup(toolWindow: ToolWindow, contentFactory: ContentFactory): DefaultActionGroup {
        val addFileContentAction = object : FiskeoyeAction("File Content") {
            override fun actionPerformed(e: AnActionEvent) {
                fileContentCounter++
                val newFileContent = contentFactory.createContent(
                    FileContentPanel(),
                    "File Content ($fileContentCounter)",
                    false
                ).apply {
                    isCloseable = true
                    putUserData(CONTENT_TYPE_KEY, ContentType.FILE_CONTENT)
                }
                toolWindow.contentManager.addContent(newFileContent)
                toolWindow.contentManager.setSelectedContent(newFileContent, true)
            }
        }

        val addFilenameAction = object : FiskeoyeAction("File Name") {
            override fun actionPerformed(e: AnActionEvent) {
                filenameCounter++
                val newFilename = contentFactory.createContent(
                    FilenamePanel(),
                    "File Name ($filenameCounter)",
                    false
                ).apply {
                    isCloseable = true
                    putUserData(CONTENT_TYPE_KEY, ContentType.FILENAME)
                }
                toolWindow.contentManager.addContent(newFilename)
                toolWindow.contentManager.setSelectedContent(newFilename, true)
            }
        }

        return DefaultActionGroup("New Fiskeoye Tab", true).apply {
            templatePresentation.icon = Add
            add(addFileContentAction)
            add(addFilenameAction)
        }
    }

    private enum class ContentType { FILE_CONTENT, FILENAME }
    private companion object {
        val CONTENT_TYPE_KEY = com.intellij.openapi.util.Key.create<ContentType>("FISKEOYE_CONTENT_TYPE")
    }
}