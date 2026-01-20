package no.spk.fiskeoye.plugin.actions.window.filter

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.component.LabelIcon
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Bitbucket
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Github
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Warning
import no.spk.fiskeoye.plugin.util.update
import javax.swing.Icon
import javax.swing.RowFilter
import javax.swing.table.TableRowSorter

abstract class FilterAction(open val filterState: FilterState, open val table: JBTable, text: String, icon: Icon) : ToggleAction(text, "", icon) {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    fun filter() {
        if (table.model.rowCount == 0) return
        val sorter = table.rowSorter as? TableRowSorter<*> ?: TableRowSorter(table.model).also {
            table.rowSorter = it
        }
        applyFilter(sorter)
        val maxWidth = table.getClientProperty("TABLE_MAX_WIDTH_KEY") as Int
        table.update(maxWidth)
    }

    private fun applyFilter(sorter: TableRowSorter<*>) {
        // Create a custom row filter based on your criteria
        sorter.rowFilter = object : RowFilter<Any, Int>() {

            override fun include(entry: Entry<out Any, out Int>): Boolean {
                // Adjust column indices based on your table structure
                // Example assumes columns: 0=source, 1=type, etc.

                val sourceValue = entry.getValue(0) as LabelIcon  // Adjust column index

                if (filterState.bitbucketIsSelected && sourceValue.icon == Bitbucket) {
                    return false
                }

                if (filterState.githubIsSelected && sourceValue.icon == Github) {
                    return false
                }

                if (filterState.warningIsSelected && sourceValue.icon == Warning) {
                    return false
                }

                return true
            }
        }
    }

}