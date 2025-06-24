package no.spk.fiskeoye.plugin.actions.search

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.util.getFileNamePanel

internal class FilenameSearchAction : SearchAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val selected = getCurrentCaret(e)?.selectedText ?: (getCurrentFile(e)?.name ?: return)
        val filenamePanel = getFileNamePanel()
        show(e, filenamePanel)
        filenamePanel.apply {
            includeField.apply {
                requestFocusInWindow()
                text = selected
            }
        }
    }

}
