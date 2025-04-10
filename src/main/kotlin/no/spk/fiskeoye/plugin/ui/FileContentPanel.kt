package no.spk.fiskeoye.plugin.ui

import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.actions.window.CustomFiskeoyeAction
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons
import no.spk.fiskeoye.plugin.listeners.button.FileContentClearListener
import no.spk.fiskeoye.plugin.listeners.button.FileContentSearchListener
import no.spk.fiskeoye.plugin.listeners.toggle.ToggleKeyListener
import javax.swing.JComboBox
import javax.swing.JToggleButton

internal class FileContentPanel : FiskeoyePanel() {

    internal val mainTable: JBTable
    internal val includeField: JBTextField
    internal val excludeButton: JToggleButton
    internal val excludeField: JBTextField
    internal val caseSensitiveButton: JToggleButton
    internal val sortByCombobox: JComboBox<String>
    internal val urlLabel: JBLabel

    init {
        this.mainTable = buildTable()
        this.includeField = buildTextField(50, FileContentSearchListener(this))
        this.excludeButton = JToggleButton().apply {
            icon = FiskeoyeIcons.ExclMark
            selectedIcon = FiskeoyeIcons.ExclMarkSelected
            rolloverIcon = FiskeoyeIcons.ExclMarkHovered
            disabledSelectedIcon = FiskeoyeIcons.ExclMark
            toolTipText = "Exclude"
            addKeyListener(ToggleKeyListener(this))
        }
        this.excludeField = buildTextField(30, FileContentSearchListener(this))
        this.caseSensitiveButton = buildCaseSensitiveButton()
        this.sortByCombobox = buildSortByComboBox()
        this.urlLabel = buildUrlLabel()

        val searchButton = buildSeachButton(FileContentSearchListener(this))
        val clearButton = buildClearButton(FileContentClearListener(this))

        val defaultActionGroup = DefaultActionGroup().apply {
            add(CustomFiskeoyeAction(includeField))
            add(CustomFiskeoyeAction(excludeButton))
            add(CustomFiskeoyeAction(excludeField))
            add(CustomFiskeoyeAction(caseSensitiveButton))
            add(CustomFiskeoyeAction(sortByCombobox))
            add(CustomFiskeoyeAction(searchButton))
            add(CustomFiskeoyeAction(clearButton))
        }
        this.toolbar = buildToolbar("FileContent Navigator Toolbar", defaultActionGroup).apply {
            this.targetComponent = this@FileContentPanel
        }.component

        val subActionGroup = buildSubToolbar(urlLabel, mainTable)
        val mainPanel = SimpleToolWindowPanel(false, false).apply {
            this.toolbar = buildToolbar("FileContent Sub Navigator Toolbar", subActionGroup, true).apply {
                this.targetComponent = this@FileContentPanel
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
