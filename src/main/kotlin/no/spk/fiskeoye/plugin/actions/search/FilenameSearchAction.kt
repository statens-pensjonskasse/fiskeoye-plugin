package no.spk.fiskeoye.plugin.actions.search

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.util.getFileNamePanel
import no.spk.fiskeoye.plugin.util.getService
import no.spk.fiskeoye.plugin.util.handleSpecialChar

internal class FilenameSearchAction : SearchAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selected = getCurrentCaret(e)?.selectedText ?: (getCurrentFile(e)?.name ?: return)
        val filenamePanel = getFileNamePanel()
        show(e, filenamePanel)
        filenamePanel.apply {
            includeField.apply {
                requestFocusInWindow()
                text = if (getService().handleSpecialChar) {
                    selected.handleSpecialChar()
                } else {
                    selected
                }
            }
        }
    }

}
