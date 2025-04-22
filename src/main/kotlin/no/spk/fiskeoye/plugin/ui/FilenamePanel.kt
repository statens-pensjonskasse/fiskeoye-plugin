package no.spk.fiskeoye.plugin.ui

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.actions.window.CustomFiskeoyeAction
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons
import no.spk.fiskeoye.plugin.listeners.button.FilenameClearListener
import no.spk.fiskeoye.plugin.listeners.button.FilenameSearchListener
import no.spk.fiskeoye.plugin.listeners.toggle.ToggleKeyListener
import javax.swing.JToggleButton

internal class FilenamePanel : FiskeoyePanel() {

    internal val mainTable: JBTable
    internal val includeField: JBTextField
    internal val caseSensitiveButton: JToggleButton
    internal val searchInFullPathButton: JToggleButton
    internal val urlLabel: JBLabel

    init {
        this.mainTable = buildTable()
        this.includeField = buildTextField(50, FilenameSearchListener(this))
        this.caseSensitiveButton = buildCaseSensitiveButton()
        this.searchInFullPathButton = JToggleButton().apply {
            icon = FiskeoyeIcons.FindInFull
            selectedIcon = FiskeoyeIcons.FindInFullSelected
            rolloverIcon = FiskeoyeIcons.FindInFullHovered
            disabledSelectedIcon = FiskeoyeIcons.FindInFull
            toolTipText = "Search in full path"
            addKeyListener(ToggleKeyListener(this))
        }
        this.urlLabel = buildUrlLabel()

        val searchButton = buildSeachButton(FilenameSearchListener(this))
        val clearButton = buildClearButton(FilenameClearListener(this))

        val defaultActionGroup = DefaultActionGroup().apply {
            add(CustomFiskeoyeAction(includeField))
            add(CustomFiskeoyeAction(caseSensitiveButton))
            add(CustomFiskeoyeAction(searchInFullPathButton))
            add(CustomFiskeoyeAction(searchButton))
            add(CustomFiskeoyeAction(clearButton))
        }
        this.toolbar = buildToolbar("Filename Navigator Toolbar", defaultActionGroup).apply {
            this.targetComponent = this@FilenamePanel
        }.component

        val subActionGroup = buildSubToolbar(urlLabel, mainTable)
        val mainPanel = SimpleToolWindowPanel(false, false).apply {
            this.toolbar = buildToolbar("FileName Sub Navigator Toolbar", subActionGroup, true).apply {
                this.targetComponent = this@FilenamePanel
            }.component
            this.add(JBScrollPane(mainTable))
        }
        this.add(mainPanel)
    }

    override fun update() {
        mainTable.font = buildFont()
        mainTable.updateUI()
    }

}
