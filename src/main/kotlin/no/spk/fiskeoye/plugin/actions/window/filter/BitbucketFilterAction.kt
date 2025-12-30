package no.spk.fiskeoye.plugin.actions.window.filter

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Bitbucket

internal class BitbucketFilterAction(
    override val filterState: FilterState,
    override val table: JBTable,
) : FilterAction(filterState, table, "Bitbucket", Bitbucket) {

    override fun isSelected(e: AnActionEvent): Boolean = filterState.bitbucketIsSelected

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        filterState.bitbucketIsSelected = state
        filter()
    }

}