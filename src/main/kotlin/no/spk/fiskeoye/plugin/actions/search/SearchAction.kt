package no.spk.fiskeoye.plugin.actions.search

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.vfs.VirtualFile
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.ui.FiskeoyePanel
import no.spk.fiskeoye.plugin.util.getToolWindow

internal abstract class SearchAction : FiskeoyeAction() {

    protected fun getCurrentCaret(e: AnActionEvent): Caret? = e.getData(CommonDataKeys.EDITOR)?.caretModel?.currentCaret

    protected fun getCurrentFile(e: AnActionEvent): VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = getCurrentCaret(e)?.hasSelection() ?: false || e.getData(CommonDataKeys.VIRTUAL_FILE)?.isValid ?: false
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
