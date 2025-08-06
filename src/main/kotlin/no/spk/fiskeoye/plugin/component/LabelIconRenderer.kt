package no.spk.fiskeoye.plugin.component

import org.jdesktop.swingx.renderer.DefaultTableRenderer
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable


internal class LabelIconRenderer() : DefaultTableRenderer() {

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component? {
        val label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel
        return label.apply {
            if (value is LabelIcon) {
                setIcon(value.icon)
                text = value.text
                toolTipText = value.message
            }
        }
    }

}