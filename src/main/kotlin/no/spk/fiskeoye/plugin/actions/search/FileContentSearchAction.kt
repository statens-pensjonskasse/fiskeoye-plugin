package no.spk.fiskeoye.plugin.actions.search

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.util.getFileContentPanel

internal class FileContentSearchAction : SearchAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedText = getCurrentCaret(e)?.selectedText ?: (getCurrentFile(e)?.nameWithoutExtension ?: return)
        val fileContentPanel = getFileContentPanel()
        show(e, fileContentPanel)
        fileContentPanel.apply {
            includeField.apply {
                requestFocusInWindow()
                text = selectedText
            }
        }
    }

}
