package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.ide.ActivityTracker
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFileContent
import no.spk.fiskeoye.plugin.service.api.FiskeoyeHit
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.util.addMessage
import no.spk.fiskeoye.plugin.util.clear
import no.spk.fiskeoye.plugin.util.getDefaultModel
import no.spk.fiskeoye.plugin.util.getHeaderText
import no.spk.fiskeoye.plugin.util.getHtml
import no.spk.fiskeoye.plugin.util.getInvalidString
import no.spk.fiskeoye.plugin.util.getProject
import no.spk.fiskeoye.plugin.util.getService
import no.spk.fiskeoye.plugin.util.escapeHtml
import no.spk.fiskeoye.plugin.util.getTruckMessage
import no.spk.fiskeoye.plugin.util.hitLink
import no.spk.fiskeoye.plugin.util.hideColumns
import no.spk.fiskeoye.plugin.util.htmlToText
import no.spk.fiskeoye.plugin.util.makeLabelIcon
import no.spk.fiskeoye.plugin.util.makeUrl
import no.spk.fiskeoye.plugin.util.redText
import no.spk.fiskeoye.plugin.util.stringWidth
import no.spk.fiskeoye.plugin.util.update
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

internal class FileContentSearchListener(private val fileContentPanel: FileContentPanel) : FiskoyeSearchActionListener() {

    override fun operation() {
        val searchText = fileContentPanel.includeField.text.trim()

        if (!isValidSearchText(searchText)) {
            if (searchText.isEmpty()) return
            fileContentPanel.mainTable.addMessage(getInvalidString())
            return
        }

        performSearch(searchText)
    }

    override fun performSearch(searchText: String) {
        object : Task.Backgroundable(getProject(), "Searching file content using fiskeoye...", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true

                try {
                    maxWidth = 0
                    val searchParams = getFileContentSearchParameters()
                    val result = getFileContent(
                        searchText,
                        searchParams.isExclude,
                        searchParams.excludeText,
                        searchParams.isCaseSensitive
                    )

                    if (result.hits == null) {
                        showErrorMessage(fileContentPanel.mainTable, result.message)
                        return
                    }

                    updateUiWithResults(searchText, searchParams, result.url, result.hits)

                } catch (e: Exception) {
                    handleSearchError(fileContentPanel.mainTable, e)
                }
            }
        }.queue()
    }

    private fun getFileContentSearchParameters(): FileContentSearchParameters {
        return fileContentPanel.run {
            FileContentSearchParameters(
                isExclude = excludeButton.isSelected,
                excludeText = excludeField.text,
                isCaseSensitive = caseSensitiveButton.isSelected
            )
        }
    }

    private fun updateUiWithResults(
        searchText: String,
        searchParams: FileContentSearchParameters,
        requestUrl: String,
        hits: List<FiskeoyeHit>
    ) {
        SwingUtilities.invokeLater {
            val headerText = buildHeaderText(searchText, searchParams, hits.size)
            val model = buildTableModel(headerText, hits)

            fileContentPanel.apply {
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
        searchParams: FileContentSearchParameters,
        resultCount: Int
    ): String {
        val baseHeader = getHeaderText(
            searchText,
            searchParams.isExclude,
            searchParams.excludeText,
            searchParams.isCaseSensitive
        )
        return "$baseHeader Antall treff: $resultCount"
    }

    private fun buildTableModel(headerText: String, hits: List<FiskeoyeHit>) =
        getDefaultModel(headerText).apply {
            val service = getService()
            val truncSize = service.truncSize
            val codeLength = service.codeLength
            var count = 0

            if (hits.isNotEmpty()) {
                hits.forEach { hit ->
                    if (count >= truncSize) return@forEach

                    val searchResult = toSearchResultEntry(hit, codeLength)
                    if (searchResult != null) {
                        addTableRow(searchResult)
                        count++
                    }
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

    private fun toSearchResultEntry(hit: FiskeoyeHit, codeLength: Int): SearchResultEntry {
        val rawCode = hit.match.orEmpty()
        val code = if (rawCode.length > codeLength) rawCode.take(codeLength) + "..." else rawCode
        val line = hit.lineno?.toString().orEmpty()

        return SearchResultEntry(
            file = hit.filename,
            line = line,
            code = code,
            url = hit.url,
            displayText = "${hit.filename}:$line"
        )
    }

    private fun DefaultTableModel.addTableRow(result: SearchResultEntry) {
        val html = getHtml("${hitLink(result.url, result.file)}:${redText(result.line)}:${escapeHtml(result.code)}")
        val htmlWidth = fileContentPanel.mainTable.stringWidth(htmlToText(html))
        maxWidth = checkWidth(maxWidth, htmlWidth)

        addRow(
            arrayOf(
                makeLabelIcon(result.url, html),
                makeUrl(result.url),
                result.displayText
            )
        )
    }

    private data class FileContentSearchParameters(
        val isExclude: Boolean,
        val excludeText: String,
        val isCaseSensitive: Boolean
    )

    private data class SearchResultEntry(
        val file: String,
        val line: String,
        val code: String,
        val url: String,
        val displayText: String
    )

}