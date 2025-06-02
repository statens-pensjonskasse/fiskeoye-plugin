package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFilename
import no.spk.fiskeoye.plugin.ui.FilenamePanel
import no.spk.fiskeoye.plugin.util.addMessage
import no.spk.fiskeoye.plugin.util.clear
import no.spk.fiskeoye.plugin.util.getDefaultModel
import no.spk.fiskeoye.plugin.util.getGeneralErrorMessage
import no.spk.fiskeoye.plugin.util.getHeaderText
import no.spk.fiskeoye.plugin.util.getInvalidString
import no.spk.fiskeoye.plugin.util.getService
import no.spk.fiskeoye.plugin.util.getTruckMessage
import no.spk.fiskeoye.plugin.util.hideColumns
import no.spk.fiskeoye.plugin.util.makeLabelIcon
import no.spk.fiskeoye.plugin.util.makeUrl
import java.util.concurrent.CompletableFuture.supplyAsync

internal class FilenameSearchListener(private val filenamePanel: FilenamePanel) : FiskeoyeActionListener() {

    private val logger: Logger = Logger.getInstance(FilenameSearchListener::class.java)

    override fun operation() {
        filenamePanel.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val includeText = includeField.text
                if (includeText.trim().isEmpty()) return@launch

                val isCaseSensitive = caseSensitiveButton.isSelected
                val isSearchInFullPath = searchInFullPathButton.isSelected

                if (includeText.length < 3) {
                    mainTable.addMessage(getInvalidString())
                    return@launch
                }

                val (requestUrl, elements) = supplyAsync { getFilename(includeText, isCaseSensitive, isSearchInFullPath) }.get()
                if (elements == null) {
                    mainTable.addMessage(getGeneralErrorMessage())
                    return@launch
                }

                val headerText = getHeaderText(includeText, isCaseSensitive, isSearchInFullPath) + " Antall treff: ${elements.size}"
                logger.info(headerText)

                val truncSize = getService().truncSize
                val model = getDefaultModel(headerText)
                var count = 0
                if (elements.isNotEmpty()) {
                    elements.forEach { element ->
                        if (count > truncSize) return@forEach
                        val html = "<html>${element}</html>"
                        val url = element.attr("href")
                        val text = element.text()
                        model.addRow(arrayOf(makeLabelIcon(url, html), makeUrl(url), text))
                        count++
                    }
                }

                mainTable.clear()
                if (count >= truncSize) {
                    model.setColumnIdentifiers(arrayOf("<html>$headerText. ${getTruckMessage(truncSize)}</html>", "url", "text"))
                }
                mainTable.model = model
                mainTable.hideColumns()
                urlLabel.text = requestUrl
            }
        }
    }

}
