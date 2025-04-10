package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.components.JBLabel
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.OpenInBrowser
import no.spk.fiskeoye.plugin.util.makeUrl
import no.spk.fiskeoye.plugin.util.openUrlWithBrowser

internal class OpenInBrowserAction(
    private val urlLabel: JBLabel,
    override val table: JBTable
) : TableAction(table, "Open in browser", OpenInBrowser) {

    override fun actionPerformed(e: AnActionEvent) {
        val url = makeUrl(urlLabel.text) ?: return
        openUrlWithBrowser(url)
    }

}
