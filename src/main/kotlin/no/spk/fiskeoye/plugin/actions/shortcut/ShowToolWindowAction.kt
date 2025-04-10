package no.spk.fiskeoye.plugin.actions.shortcut

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.util.getFileContentPanel
import no.spk.fiskeoye.plugin.util.getToolWindow

internal class ShowToolWindowAction : FiskeoyeAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val toolWindow = getToolWindow(project)
        if (toolWindow.isVisible) {
            toolWindow.hide()
            return
        }
        toolWindow.show()
        getFileContentPanel().includeField.requestFocusInWindow()
    }

}
