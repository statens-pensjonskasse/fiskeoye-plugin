package no.spk.fiskeoye.plugin.component

import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer


internal class LabelIconRenderer : DefaultTableCellRenderer() {

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column)
        if (value is LabelIcon) {
            icon = value.icon
            text = value.text
            toolTipText = value.message
        } else {
            icon = null
            text = value?.toString()
            toolTipText = null
        }
        return this
    }

}
