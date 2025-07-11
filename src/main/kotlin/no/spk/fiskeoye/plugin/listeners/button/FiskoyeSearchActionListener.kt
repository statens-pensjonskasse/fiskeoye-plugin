package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.util.addMessage
import no.spk.fiskeoye.plugin.util.getGeneralErrorMessage
import javax.swing.SwingUtilities

internal abstract class FiskoyeSearchActionListener : FiskeoyeActionListener() {

    private val logger: Logger = Logger.getInstance(FiskoyeSearchActionListener::class.java)

    protected var maxWidth = 0

    protected abstract fun performSearch(searchText: String)

    protected fun isValidSearchText(text: String): Boolean = text.isNotEmpty() && text.length >= 3

    protected fun showErrorMessage(mainTable: JBTable) = SwingUtilities.invokeLater {
        mainTable.addMessage(getGeneralErrorMessage())
    }

    protected fun handleSearchError(mainTable: JBTable, exception: Exception) = SwingUtilities.invokeLater {
        logger.error("Search failed: ${exception.message}", exception)
        mainTable.addMessage(getGeneralErrorMessage())
    }

    protected fun checkWidth(width: Int, htmlWidth: Int): Int {
        return if (width < htmlWidth) {
            htmlWidth
        } else {
            width
        }
    }

}
