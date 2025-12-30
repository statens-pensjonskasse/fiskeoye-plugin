package no.spk.fiskeoye.plugin.actions.window.filter

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Warning

internal class WarningFilterAction(
    override val filterState: FilterState,
    override val table: JBTable,
) : FilterAction(filterState, table, "Warning", Warning) {

    override fun isSelected(e: AnActionEvent): Boolean = filterState.warningIsSelected

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        filterState.warningIsSelected = state
        filter()
    }

}