package no.spk.fiskeoye.plugin.listeners.table

import com.intellij.ui.table.JBTable
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

internal class TableCellKeyListener : TableCellListener(), KeyListener {

    override fun keyPressed(e: KeyEvent) {
        val table = e.component as? JBTable ?: return
        val row = table.selectedRow
        val col = table.selectedColumn + 1
        if (e.keyCode == KeyEvent.VK_ENTER) {
            openUrl(table, row, col)
        }
    }

    override fun keyTyped(e: KeyEvent) = Unit

    override fun keyReleased(e: KeyEvent) = Unit

}