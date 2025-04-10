package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import javax.swing.Icon

internal abstract class TableAction(
    open val table: JBTable,
    override val toolTip: String,
    override val icon: Icon
) : FiskeoyeAction(toolTip, icon) {

    override fun update(e: AnActionEvent) {
        super.update(e)
        if (e.project == null) return
        e.presentation.isEnabled = !table.isEmpty
    }

}
