package no.spk.fiskeoye.plugin.actions.editor

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.util.getFileContentPanel

internal class FileContentEditorSearchAction : EditorSearchAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedText = getCurrentCaret(e)?.selectedText ?: return
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
