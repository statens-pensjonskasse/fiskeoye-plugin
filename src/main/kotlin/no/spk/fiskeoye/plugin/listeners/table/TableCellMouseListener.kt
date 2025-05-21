package no.spk.fiskeoye.plugin.listeners.table

import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JTable

internal class TableCellMouseListener : TableCellListener(), MouseListener {

    override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount != 2) return
        val table = e.component as? JTable ?: return
        val row = table.selectedRow
        val col = table.selectedColumn + 1
        openUrl(table, row, col)
    }

    override fun mouseExited(e: MouseEvent) = Unit

    override fun mousePressed(e: MouseEvent) = Unit

    override fun mouseReleased(e: MouseEvent) = Unit

    override fun mouseEntered(e: MouseEvent) = Unit

}