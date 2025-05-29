package no.spk.fiskeoye.plugin.util

import com.intellij.idea.LoggerFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.component.LabelIcon
import no.spk.fiskeoye.plugin.enum.ScrollDirection
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Bitbucket
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Github
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Warning
import no.spk.fiskeoye.plugin.settings.FiskeoyeState
import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.ui.FilenamePanel
import no.spk.fiskeoye.plugin.ui.FiskeoyePanel
import java.awt.Desktop
import java.awt.Window
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.URL
import javax.swing.table.DefaultTableModel

internal fun scrollTo(table: JBTable, direction: ScrollDirection) {
    val rowIndex = when (direction) {
        ScrollDirection.END -> table.rowCount - 1
        ScrollDirection.TOP -> 0
    }
    if (table.rowCount == 0 || rowIndex > table.rowCount) return
    table.changeSelection(rowIndex, 0, false, false)
}

internal fun copy(textToCopy: String) {
    CopyPasteManager.getInstance().setContents(StringSelection(textToCopy))
}

internal fun getToolWindowPanel(project: Project, index: Int): FiskeoyePanel {
    return getToolWindow(project).contentManager.getContent(index)!!.component as FiskeoyePanel
}

internal fun getToolWindow(project: Project): ToolWindow {
    return ToolWindowManager.getInstance(project).getToolWindow("Fiskeoye")!!
}

internal fun getFileContentPanel(): FileContentPanel = getToolWindowPanel(getProject(), 0) as FileContentPanel

internal fun getFileNamePanel(): FilenamePanel = getToolWindowPanel(getProject(), 1) as FilenamePanel

internal fun getProject(): Project {
    val projects = ProjectManager.getInstance().openProjects
    var activeProject: Project? = null
    for (project in projects) {
        val window: Window? = WindowManager.getInstance().suggestParentWindow(project)
        if (window != null && window.isActive) {
            activeProject = project
        }
    }
    return activeProject!!
}

internal fun getService() = ApplicationManager.getApplication().getService(FiskeoyeState::class.java)

internal fun makeUrl(spec: String) = runCatching { URI(spec).toURL() }.getOrNull()

internal fun makeLabelIcon(url: String, html: String): LabelIcon {
    val urlString = url.lowercase().trim()
    return if (urlString.startsWith("http")) {
        if (urlString.startsWith("https://github")) {
            LabelIcon(html, Github)
        } else {
            LabelIcon(html, Bitbucket)
        }
    } else {
        LabelIcon(html, Warning, "NB! Url is broken")
    }
}

internal fun openUrlWithBrowser(url: URL) = openUrlWithBrowser(url.toURI())

internal fun openUrlWithBrowser(uri: URI) {
    if (Desktop.isDesktopSupported()) {
        runCatching {
            Desktop.getDesktop().browse(uri)
        }.onFailure {
            Logger.getInstance("FiskeoyeUtil").error(it)
        }
    }
}

internal fun JBTable.clear() {
    this.model = DefaultTableModel()
}

internal fun JBTable.addMessage(message: String) {
    this.apply {
        clear()
        model = getDefaultModel(message)
        hideColumns()
    }
}

internal fun JBTable.hideColumns() {
    this.apply {
        removeColumn(getColumn("url"))
        removeColumn(getColumn("text"))
    }
}

internal fun Boolean.toOnOff(): String = if (this) "on" else "off"

internal fun getHeaderText(include: String, isExcluded: Boolean, exclude: String, isCaseSensitive: Boolean): String {
    if (include.trim().isEmpty()) return ""
    if (isExcluded && exclude.isEmpty() && isCaseSensitive) {
        return "Case-sensitive Søkeresultat for '$include'."
    }
    if (isExcluded && exclude.isNotEmpty() && isCaseSensitive) {
        return "Case-sensitive Søkeresultat for '$include' som IKKE inneholder '$exclude'."
    }
    if (isExcluded && exclude.isNotEmpty() && !isCaseSensitive) {
        return "Søkeresultat for '$include' som IKKE inneholder '$exclude'."
    }
    if (isExcluded && exclude.isEmpty() && !isCaseSensitive) {
        return "Søkeresultat for '$include'."
    }
    if (!isExcluded && exclude.isNotEmpty() && !isCaseSensitive) {
        return "Søkeresultat for '$include' som også inneholder '$exclude'."
    }
    if (!isExcluded && exclude.isEmpty() && !isCaseSensitive) {
        return "Søkeresultat for '$include'."
    }
    if (!isExcluded && exclude.isNotEmpty() && isCaseSensitive) {
        return "Case-sensitive Søkeresultat for '$include' som også inneholder '$exclude'."
    }
    return "Case-sensitive Søkeresultat for '$include'."
}

internal fun getHeaderText(include: String, isCaseSensitive: Boolean, isSearchInFullPath: Boolean): String {
    if (include.trim().isEmpty()) return ""
    if (isCaseSensitive) {
        return if (isSearchInFullPath) {
            "Case-sensitive Søkeresultat for filer med '$include' i navnet eller i filstien."
        } else {
            "Case-sensitive Søkeresultat for filer med '$include' i navnet."
        }
    }
    return if (!isSearchInFullPath) {
        "Søkeresultat for filer med '$include' i navnet."
    } else {
        "Søkeresultat for filer med '$include' i navnet eller i filstien."
    }
}

internal fun getDefaultModel(header: String): DefaultTableModel {
    return object : DefaultTableModel(arrayOf(header, "url", "text"), 0) {
        override fun getColumnClass(column: Int) = when (column) {
            0 -> LabelIcon::class.java
            1 -> URL::class.java
            2 -> String::class.java
            else -> super.getColumnClass(column)
        }

        override fun isCellEditable(
            row: Int,
            col: Int,
        ) = false
    }
}

internal fun getTruckMessage(maxSize: Int): String {
    return getHtmlError("NB! Antall treff overstiger $maxSize. Resultatet er trunkert")
}

internal fun getGeneralErrorMessage(): String {
    return getHtmlError("Ops! Ser ut som noe gikk galt! Se \"${LoggerFactory.getLogFilePath()}\" for mer info...")
}

internal fun getInvalidString(): String {
    return getHtmlError("Søkestrengen må være på minst tre tegn.")
}

internal fun getHtmlError(value: String): String {
    return "<html><span style=\"color:red;\">$value<span><html>"
}