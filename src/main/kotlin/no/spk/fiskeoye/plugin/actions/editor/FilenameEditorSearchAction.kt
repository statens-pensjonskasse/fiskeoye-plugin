package no.spk.fiskeoye.plugin.actions.editor

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.util.getFileNamePanel

internal class FilenameEditorSearchAction : EditorSearchAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selectedText = getCurrentCaret(e)?.selectedText ?: return
        val filenamePanel = getFileNamePanel()
        show(e, filenamePanel)
        filenamePanel.apply {
            includeField.apply {
                requestFocusInWindow()
                text = selectedText
            }
        }
    }

}
