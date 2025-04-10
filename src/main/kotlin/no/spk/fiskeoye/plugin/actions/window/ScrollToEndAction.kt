package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.enum.ScrollDirection
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.ScrollDown
import no.spk.fiskeoye.plugin.util.scrollTo

internal class ScrollToEndAction(
    override val table: JBTable
) : TableAction(table, "Scroll to end", ScrollDown) {

    override fun actionPerformed(e: AnActionEvent) = scrollTo(table, ScrollDirection.END)

}
