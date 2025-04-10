package no.spk.fiskeoye.plugin.actions.editor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.ui.FiskeoyePanel
import no.spk.fiskeoye.plugin.util.getToolWindow

internal abstract class EditorSearchAction : FiskeoyeAction() {

    protected fun getCurrentCaret(e: AnActionEvent): Caret? = e.getData(CommonDataKeys.EDITOR)?.caretModel?.currentCaret

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = getCurrentCaret(e)?.hasSelection() ?: false
    }

    protected fun show(e: AnActionEvent, fiskeoyePanel: FiskeoyePanel) {
        if (e.project == null) return
        val toolWindow = getToolWindow(e.project!!)
        if (!toolWindow.isVisible) {
            toolWindow.show()
        }
        val contentManager = toolWindow.contentManager
        val content = contentManager.getContent(fiskeoyePanel)
        contentManager.setSelectedContent(content)
    }
}
