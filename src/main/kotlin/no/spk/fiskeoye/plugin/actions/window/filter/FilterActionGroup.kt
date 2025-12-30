package no.spk.fiskeoye.plugin.actions.window.filter

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Filter

internal class FilterActionGroup(mainTable: JBTable) : DefaultActionGroup("Filter Away", true) {

    init {
        templatePresentation.icon = Filter
        val filterState = FilterState()
        mainTable.putClientProperty("FILTER_STATE", filterState)

        add(BitbucketFilterAction(filterState, mainTable))
        add(GithubFilterAction(filterState, mainTable))
        add(WarningFilterAction(filterState, mainTable))
    }

}