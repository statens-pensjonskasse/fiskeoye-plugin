package no.spk.fiskeoye.plugin.actions.window.filter

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Github

internal class GithubFilterAction(
    override val filterState: FilterState,
    override val table: JBTable,
) : FilterAction(filterState, table, "Github", Github) {

    override fun isSelected(e: AnActionEvent): Boolean = filterState.githubIsSelected

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        filterState.githubIsSelected = state
        filter()
    }

}