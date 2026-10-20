package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.ide.ActivityTracker
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFilename
import no.spk.fiskeoye.plugin.service.api.FiskeoyeHit
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
import no.spk.fiskeoye.plugin.util.hitLink
import no.spk.fiskeoye.plugin.util.hideColumns
import no.spk.fiskeoye.plugin.util.htmlToText
import no.spk.fiskeoye.plugin.util.makeLabelIcon
import no.spk.fiskeoye.plugin.util.makeUrl
import no.spk.fiskeoye.plugin.util.stringWidth
import no.spk.fiskeoye.plugin.util.update

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
                    val result = getFilename(
                        searchText,
                        searchParams.isCaseSensitive,
                        searchParams.isSearchInFullPath
                    )

                    if (result.hits == null) {
                        showErrorMessage(filenamePanel.mainTable, result.message)
                        return
                    }

                    updateUiWithResults(searchText, searchParams, result.url, result.hits)

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
        hits: List<FiskeoyeHit>
    ) {
        SwingUtilities.invokeLater {
            val headerText = buildHeaderText(searchText, searchParams, hits.size)
            val model = buildTableModel(headerText, hits)

            filenamePanel.apply {
                mainTable.putClientProperty("TABLE_MAX_WIDTH_KEY", maxWidth)
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

    private fun buildTableModel(headerText: String, hits: List<FiskeoyeHit>) =
        getDefaultModel(headerText).apply {
            val truncSize = getService().truncSize
            var count = 0

            if (hits.isNotEmpty()) {
                hits.forEach { hit ->
                    if (count >= truncSize) return@forEach

                    addTableRow(hit)
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

    private fun DefaultTableModel.addTableRow(hit: FiskeoyeHit) {
        val html = getHtml(hitLink(hit.url, hit.filename))
        val htmlWidth = filenamePanel.mainTable.stringWidth(htmlToText(html))
        maxWidth = checkWidth(maxWidth, htmlWidth)
        val url = hit.url
        val text = hit.filename

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