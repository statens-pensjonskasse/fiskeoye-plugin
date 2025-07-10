package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.ide.ActivityTracker
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFilename
import no.spk.fiskeoye.plugin.ui.FilenamePanel
import no.spk.fiskeoye.plugin.util.addMessage
import no.spk.fiskeoye.plugin.util.clear
import no.spk.fiskeoye.plugin.util.getDefaultModel
import no.spk.fiskeoye.plugin.util.getHeaderText
import no.spk.fiskeoye.plugin.util.getHtml
import no.spk.fiskeoye.plugin.util.getInvalidString
import no.spk.fiskeoye.plugin.util.getProject
import no.spk.fiskeoye.plugin.util.getService
import no.spk.fiskeoye.plugin.util.getTruckMessage
import no.spk.fiskeoye.plugin.util.hideColumns
import no.spk.fiskeoye.plugin.util.htmlToText
import no.spk.fiskeoye.plugin.util.makeLabelIcon
import no.spk.fiskeoye.plugin.util.makeUrl
import no.spk.fiskeoye.plugin.util.stringWidth
import no.spk.fiskeoye.plugin.util.update
import org.jsoup.nodes.Element
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

internal class FilenameSearchListener(private val filenamePanel: FilenamePanel) : FiskoyeSearchActionListener() {

    override fun operation() {
        val searchText = filenamePanel.includeField.text.trim()

        if (!isValidSearchText(searchText)) {
            if (searchText.isEmpty()) return
            filenamePanel.mainTable.addMessage(getInvalidString())
            return
        }

        performSearch(searchText)
    }

    override fun performSearch(searchText: String) {
        object : Task.Backgroundable(getProject(), "Searching for file name using fiskeoye...", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true

                try {
                    maxWidth = 0
                    val searchParams = getFilenameSearchParameters()
                    val (requestUrl, elements) = getFilename(
                        searchText,
                        searchParams.isCaseSensitive,
                        searchParams.isSearchInFullPath
                    )

                    if (elements == null) {
                        showErrorMessage(filenamePanel.mainTable)
                        return
                    }

                    updateUiWithResults(searchText, searchParams, requestUrl, elements)

                } catch (e: Exception) {
                    handleSearchError(filenamePanel.mainTable, e)
                }
            }
        }.queue()
    }

    private fun getFilenameSearchParameters(): FilenameSearchParameters {
        return filenamePanel.run {
            FilenameSearchParameters(
                isCaseSensitive = caseSensitiveButton.isSelected,
                isSearchInFullPath = searchInFullPathButton.isSelected
            )
        }
    }

    private fun updateUiWithResults(
        searchText: String,
        searchParams: FilenameSearchParameters,
        requestUrl: String,
        elements: List<Element>
    ) {
        SwingUtilities.invokeLater {
            val headerText = buildHeaderText(searchText, searchParams, elements.size)
            val model = buildTableModel(headerText, elements)

            filenamePanel.apply {
                mainTable.clear()
                mainTable.model = model
                mainTable.hideColumns()
                mainTable.update(maxWidth)
                urlLabel.text = requestUrl
                ActivityTracker.getInstance().inc()
            }
        }
    }

    private fun buildHeaderText(
        searchText: String,
        searchParams: FilenameSearchParameters,
        resultCount: Int
    ): String {
        val baseHeader = getHeaderText(
            searchText,
            searchParams.isCaseSensitive,
            searchParams.isSearchInFullPath
        )
        return "$baseHeader Antall treff: $resultCount"
    }

    private fun buildTableModel(headerText: String, elements: List<Element>) =
        getDefaultModel(headerText).apply {
            val truncSize = getService().truncSize
            var count = 0

            if (elements.isNotEmpty()) {
                elements.forEach { element ->
                    if (count >= truncSize) return@forEach

                    addTableRow(element)
                    count++
                }
            }

            if (count >= truncSize) {
                setColumnIdentifiers(
                    arrayOf(
                        getHtml("$headerText. ${getTruckMessage(truncSize)}"),
                        "url",
                        "text"
                    )
                )
            }
        }

    private fun DefaultTableModel.addTableRow(element: Element) {
        val html = getHtml("$element")
        val htmlWidth = filenamePanel.mainTable.stringWidth(htmlToText(html))
        maxWidth = checkWidth(maxWidth, htmlWidth)
        val url = element.attr("href")
        val text = element.text()

        addRow(
            arrayOf(
                makeLabelIcon(url, html),
                makeUrl(url),
                text
            )
        )
    }

    private data class FilenameSearchParameters(
        val isCaseSensitive: Boolean,
        val isSearchInFullPath: Boolean
    )
}