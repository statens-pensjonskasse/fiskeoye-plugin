package no.spk.fiskeoye.plugin.component.menu

import com.intellij.openapi.ui.JBMenuItem
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.component.LabelIcon
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Copy
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.CopyLink
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.CopyLinkForJira
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.CopyLinkForMarkdown
import no.spk.fiskeoye.plugin.util.copy
import no.spk.fiskeoye.plugin.util.htmlToText
import javax.swing.Icon

internal abstract class FiskeoyeMenuItem(open val table: JBTable, title: String, icon: Icon) : JBMenuItem(title, icon) {
    private fun selectedRow(): Int = table.selectedRow
    private fun selectedHtml(): String = (table.model.getValueAt(selectedRow(), 0) as LabelIcon).text
    protected fun selectedUrl(): String = table.model.getValueAt(selectedRow(), 1).toString()
    protected fun selectedText(): String = table.model.getValueAt(selectedRow(), 2).toString()
    protected fun selectedFullText(): String = htmlToText(selectedHtml()).trim()
}

internal class CopyTextMenuItem(override val table: JBTable) : FiskeoyeMenuItem(table, "Copy", Copy) {
    init {
        this.addActionListener {
            copy(selectedFullText())
        }
    }
}

internal class CopyLinkMenuItem(override val table: JBTable) : FiskeoyeMenuItem(table, "Copy Link", CopyLink) {
    init {
        this.addActionListener {
            copy(selectedUrl())
        }
    }
}

internal class CopyLinkForMarkdownMenuItem(override val table: JBTable) : FiskeoyeMenuItem(table, "Copy Link for Markdown", CopyLinkForMarkdown) {
    init {
        this.addActionListener {
            copy("[${selectedText()}](${selectedUrl()})")
        }
    }
}

internal class CopyLinkForJiraMenuItem(override val table: JBTable) : FiskeoyeMenuItem(table, "Copy Link for Jira", CopyLinkForJira) {
    init {
        this.addActionListener {
            copy("[${selectedText()}|${selectedUrl()}]")
        }
    }
}