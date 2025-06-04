package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.ide.ActivityTracker
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFileContent
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.util.addMessage
import no.spk.fiskeoye.plugin.util.clear
import no.spk.fiskeoye.plugin.util.getDefaultModel
import no.spk.fiskeoye.plugin.util.getHeaderText
import no.spk.fiskeoye.plugin.util.getInvalidString
import no.spk.fiskeoye.plugin.util.getProject
import no.spk.fiskeoye.plugin.util.getService
import no.spk.fiskeoye.plugin.util.getTruckMessage
import no.spk.fiskeoye.plugin.util.hideColumns
import no.spk.fiskeoye.plugin.util.makeLabelIcon
import no.spk.fiskeoye.plugin.util.makeUrl
import org.jsoup.nodes.Element
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

internal class FileContentSearchListener(private val fileContentPanel: FileContentPanel) : FiskoyeSearchActionListener() {

    private val logger: Logger = Logger.getInstance(FileContentSearchListener::class.java)

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
        object : Task.Backgroundable(getProject(), "Searching file content using fiskeoye...", false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true

                try {
                    val searchParams = getFileContentSearchParameters()
                    val (requestUrl, elements) = getFileContent(
                        searchText,
                        searchParams.isExclude,
                        searchParams.excludeText,
                        searchParams.isCaseSensitive
                    )

                    if (elements == null) {
                        showErrorMessage(fileContentPanel.mainTable)
                        return
                    }

                    updateUiWithResults(searchText, searchParams, requestUrl, elements)

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
        elements: List<Element>
    ) {
        SwingUtilities.invokeLater {
            val headerText = buildHeaderText(searchText, searchParams, elements.size)
            logger.info(headerText)

            val model = buildTableModel(headerText, elements)

            fileContentPanel.apply {
                mainTable.clear()
                mainTable.model = model
                mainTable.hideColumns()
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

    private fun buildTableModel(headerText: String, elements: List<Element>) =
        getDefaultModel(headerText).apply {
            val service = getService()
            val truncSize = service.truncSize
            val codeLength = service.codeLength
            var count = 0

            if (elements.isNotEmpty()) {
                elements.forEach { element ->
                    if (count >= truncSize) return@forEach

                    val searchResult = parseSearchElement(element, codeLength)
                    if (searchResult != null) {
                        addTableRow(searchResult)
                        count++
                    }
                }
            }

            if (count >= truncSize) {
                setColumnIdentifiers(
                    arrayOf(
                        "<html>$headerText. ${getTruckMessage(truncSize)}</html>",
                        "url",
                        "text"
                    )
                )
            }
        }

    private fun parseSearchElement(element: Element, codeLength: Int): SearchResultEntry? {
        val childNodes = element.childNodes()

        if (childNodes.size != 4) {
            logger.warn("Unexpected element structure: expected 4 child nodes, got ${childNodes.size}")
            return null
        }

        return try {
            val file = childNodes[0] as Element
            val line = childNodes[2] as Element
            val rawCode = childNodes[3].toString()

            val code = if (rawCode.length > codeLength) {
                rawCode.substring(0, codeLength) + "..."
            } else {
                rawCode
            }

            SearchResultEntry(
                file = file,
                line = line,
                code = code,
                url = file.attr("href"),
                displayText = "${file.text()}:${line.text()}"
            )
        } catch (e: ClassCastException) {
            logger.warn("Failed to parse search element: ${e.message}")
            showErrorMessage(fileContentPanel.mainTable)
            null
        }
    }

    private fun DefaultTableModel.addTableRow(result: SearchResultEntry) {
        val html = "<html>${result.file}:${result.line}:${result.code}</html>"

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
        val file: Element,
        val line: Element,
        val code: String,
        val url: String,
        val displayText: String
    )

}