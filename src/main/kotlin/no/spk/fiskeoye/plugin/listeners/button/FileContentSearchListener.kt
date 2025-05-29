package no.spk.fiskeoye.plugin.listeners.button

import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFileContent
import no.spk.fiskeoye.plugin.ui.FileContentPanel
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
import org.jsoup.nodes.Element
import java.util.concurrent.CompletableFuture.supplyAsync

internal class FileContentSearchListener(private val fileContentPanel: FileContentPanel) : FiskeoyeActionListener() {

    private val logger: Logger = Logger.getInstance(FileContentSearchListener::class.java)

    override fun operation() {
        fileContentPanel.apply {
            CoroutineScope(Dispatchers.IO).launch {
                val includeText = includeField.text
                if (includeText.trim().isEmpty()) return@launch

                val isExclude = excludeButton.isSelected
                val excludeText = excludeField.text
                val isCaseSensitive = caseSensitiveButton.isSelected

                if (includeText.length < 3) {
                    mainTable.addMessage(getInvalidString())
                    return@launch
                }

                val (requestUrl, elements) = supplyAsync { getFileContent(includeText, isExclude, excludeText, isCaseSensitive) }.get()
                if (elements == null) {
                    mainTable.addMessage(getGeneralErrorMessage())
                    return@launch
                }

                val headerText = getHeaderText(includeText, isExclude, excludeText, isCaseSensitive) + " Antall treff: ${elements.size}"
                logger.info(headerText)

                val truncSize = getService().truncSize
                val codeLength = getService().codeLength
                val model = getDefaultModel(headerText)
                var count = 0
                if (elements.isNotEmpty()) {
                    elements.forEach { element ->
                        if (count > truncSize || element.childNodes().size != 4) return@forEach
                        val file = element.childNodes()[0] as Element
                        val line = element.childNodes()[2] as Element
                        var code = element.childNodes()[3].toString()
                        if (code.length > codeLength) {
                            code = code.substring(0, codeLength) + "..."
                        }
                        val html = "<html>$file:$line:$code</html>"
                        val url = file.attr("href")
                        val text = "${file.text()}:${line.text()}"
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
