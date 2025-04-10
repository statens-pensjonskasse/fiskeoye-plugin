package no.spk.fiskeoye.plugin.listeners.table

import com.intellij.openapi.diagnostic.Logger
import no.spk.fiskeoye.plugin.util.openUrlWithBrowser
import java.net.URL
import javax.swing.JTable

internal abstract class TableCellListener {

    private val logger: Logger = Logger.getInstance(TableCellListener::class.java)

    protected fun openUrl(table: JTable, row: Int, col: Int) {
        if (isUrlColumn(table, col)) {
            logger.info("Count: ${table.rowCount} Row: $row, Column: $col")
            if (row > table.rowCount - 1 || row < 0) return
            val url = table.model.getValueAt(row, col) as? URL ?: return
            openUrlWithBrowser(url)
        }
    }

    private fun isUrlColumn(table: JTable, column: Int): Boolean {
        return column >= 0 && table.model.getColumnClass(column) == URL::class.java
    }

}
