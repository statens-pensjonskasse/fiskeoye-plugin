package no.spk.fiskeoye.plugin.ui

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.PopupMenuListenerAdapter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.actions.window.AddResultToClipboardAction
import no.spk.fiskeoye.plugin.actions.window.OpenInBrowserAction
import no.spk.fiskeoye.plugin.actions.window.ScrollToEndAction
import no.spk.fiskeoye.plugin.actions.window.ScrollToTopAction
import no.spk.fiskeoye.plugin.actions.window.SettingAction
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.CopyLink
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.CopyLinkForJira
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.CopyLinkForMarkdown
import no.spk.fiskeoye.plugin.listeners.button.FiskeoyeActionListener
import no.spk.fiskeoye.plugin.listeners.table.TableCellKeyListener
import no.spk.fiskeoye.plugin.listeners.table.TableCellMouseListener
import no.spk.fiskeoye.plugin.listeners.toggle.ToggleKeyListener
import no.spk.fiskeoye.plugin.settings.FiskeoyeState
import no.spk.fiskeoye.plugin.util.copy
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyListener
import javax.swing.JButton
import javax.swing.JToggleButton
import javax.swing.ListSelectionModel
import javax.swing.event.PopupMenuEvent

@Suppress("UnstableApiUsage")
internal abstract class FiskeoyePanel : SimpleToolWindowPanel(true, true), DumbAware {

    protected fun buildToolbar(place: String, actionGroup: ActionGroup, horizontal: Boolean = false): ActionToolbar {
        return ActionToolbarImpl(
            place,
            actionGroup,
            horizontal
        ).apply {
            layoutPolicy = ActionToolbar.AUTO_LAYOUT_POLICY
            adjustTheSameSize(true)
            setShowSeparatorTitles(true)
        }
    }

    protected fun buildSubToolbar(urlLabel: JBLabel, mainTable: JBTable): DefaultActionGroup {
        return DefaultActionGroup().apply {
            add(SettingAction())
            add(OpenInBrowserAction(urlLabel, mainTable))
            add(AddResultToClipboardAction(urlLabel, mainTable))
            add(ScrollToTopAction(mainTable))
            add(ScrollToEndAction(mainTable))
        }
    }

    protected fun buildTable(): JBTable {
        return JBTable().apply {
            tableHeader.reorderingAllowed = false
            tableHeader.resizingAllowed = false
            inheritsPopupMenu = true
            rowSelectionAllowed = true
            cellSelectionEnabled = false
            intercellSpacing = Dimension()
            componentPopupMenu = buildPopupMenu(this)
            font = buildFont()
            setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION)
            setShowGrid(false)
            setDefaultEditor(Object::class.java, null)
            addMouseListener(TableCellMouseListener())
            addKeyListener(TableCellKeyListener())
            putClientProperty("terminateEditOnFocusLost", true)
            resetDefaultFocusTraversalKeys()
        }
    }

    protected fun buildFont(): Font {
        val state = FiskeoyeState().state
        return Font(state.fontName, state.fontStyle.index, state.fontSize)
    }

    private fun buildPopupMenu(table: JBTable): JBPopupMenu {
        return JBPopupMenu().apply popupMenu@{
            val copyLink = buildCopyLink(table)
            this.add(copyLink)
            val copyLinkForMarkdown = buildCopyLinkForMarkdown(table)
            this.add(copyLinkForMarkdown)
            val copyLinkForJira = buildCopyLinkForJira(table)
            this.add(copyLinkForJira)

            this.addPopupMenuListener(object : PopupMenuListenerAdapter() {
                override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
                    copyLink.isVisible = !table.isEmpty
                    copyLinkForMarkdown.isVisible = !table.isEmpty
                    copyLinkForJira.isVisible = !table.isEmpty
                }
            })
        }
    }

    private fun buildCopyLink(table: JBTable): JBMenuItem {
        return JBMenuItem("Copy Link", CopyLink).apply {
            this.addActionListener {
                val selectedRow = table.selectedRow
                val url = table.model.getValueAt(selectedRow, 1).toString()
                copy(url)
            }
        }
    }

    private fun buildCopyLinkForMarkdown(table: JBTable): JBMenuItem {
        return JBMenuItem("Copy Link for Markdown", CopyLinkForMarkdown).apply {
            this.addActionListener {
                val selectedRow = table.selectedRow
                val url = table.model.getValueAt(selectedRow, 1).toString()
                val text = table.model.getValueAt(selectedRow, 2).toString()
                val markdown = "[$text]($url)"
                copy(markdown)
            }
        }
    }

    private fun buildCopyLinkForJira(table: JBTable): JBMenuItem {
        return JBMenuItem("Copy Link for Jira", CopyLinkForJira).apply {
            this.addActionListener {
                val selectedRow = table.selectedRow
                val url = table.model.getValueAt(selectedRow, 1).toString()
                val text = table.model.getValueAt(selectedRow, 2).toString()
                val markdown = "[$text|$url]"
                copy(markdown)
            }
        }
    }

    protected fun buildTextField(colums: Int, keyListener: KeyListener? = null): JBTextField {
        return JBTextField().apply {
            columns = colums
            if (keyListener != null) {
                addKeyListener(keyListener)
            }
        }
    }

    protected fun buildCaseSensitiveButton(): JToggleButton {
        return JToggleButton().apply {
            icon = FiskeoyeIcons.MatchCase
            selectedIcon = FiskeoyeIcons.MatchCaseSelected
            rolloverIcon = FiskeoyeIcons.MatchCaseHovered
            disabledSelectedIcon = FiskeoyeIcons.MatchCase
            toolTipText = "Case sensitive"
            addKeyListener(ToggleKeyListener(this))
        }
    }

    protected fun buildSeachButton(fiskeoyeActionListener: FiskeoyeActionListener): JButton {
        return JButton().apply {
            text = "Search"
            defaultButton()
            addActionListener(fiskeoyeActionListener)
            addKeyListener(fiskeoyeActionListener)
        }
    }

    protected fun buildClearButton(fiskeoyeActionListener: FiskeoyeActionListener): JButton {
        return JButton().apply {
            text = "Clear"
            addActionListener(fiskeoyeActionListener)
            addKeyListener(fiskeoyeActionListener)
        }
    }

    protected fun buildUrlLabel(): JBLabel {
        return JBLabel().apply {
            isVisible = false
            isFocusable = false
        }
    }

    abstract fun update()

}