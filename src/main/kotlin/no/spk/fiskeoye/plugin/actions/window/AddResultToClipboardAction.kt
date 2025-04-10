package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.components.JBLabel
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.AddResultToClipboard
import no.spk.fiskeoye.plugin.util.copy

internal class AddResultToClipboardAction(
    private val urlLabel: JBLabel,
    override val table: JBTable
) : TableAction(table, "Add result to clipboard", AddResultToClipboard) {

    override fun actionPerformed(e: AnActionEvent) = copy(urlLabel.text)

}