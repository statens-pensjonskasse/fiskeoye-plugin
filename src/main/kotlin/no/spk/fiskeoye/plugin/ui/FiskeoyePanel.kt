package no.spk.fiskeoye.plugin.ui

import com.intellij.collaboration.ui.CollaborationToolsUIUtil.defaultButton
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.PopupMenuListenerAdapter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import no.spk.fiskeoye.plugin.actions.window.AddResultToClipboardAction
import no.spk.fiskeoye.plugin.actions.window.HelpAction
import no.spk.fiskeoye.plugin.actions.window.OpenInBrowserAction
import no.spk.fiskeoye.plugin.actions.window.ScrollToEndAction
import no.spk.fiskeoye.plugin.actions.window.ScrollToTopAction
import no.spk.fiskeoye.plugin.actions.window.SettingAction
import no.spk.fiskeoye.plugin.actions.window.filter.FilterActionGroup
import no.spk.fiskeoye.plugin.component.LabelIcon
import no.spk.fiskeoye.plugin.component.LabelIconRenderer
import no.spk.fiskeoye.plugin.component.menu.CopyLinkForJiraMenuItem
import no.spk.fiskeoye.plugin.component.menu.CopyLinkForMarkdownMenuItem
import no.spk.fiskeoye.plugin.component.menu.CopyLinkMenuItem
import no.spk.fiskeoye.plugin.component.menu.CopyTextMenuItem
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons
import no.spk.fiskeoye.plugin.listeners.button.FiskeoyeActionListener
import no.spk.fiskeoye.plugin.listeners.table.TableCellKeyListener
import no.spk.fiskeoye.plugin.listeners.table.TableCellMouseListener
import no.spk.fiskeoye.plugin.listeners.toggle.ToggleKeyListener
import no.spk.fiskeoye.plugin.settings.FiskeoyeState
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyListener
import javax.swing.JButton
import javax.swing.JTable.AUTO_RESIZE_OFF
import javax.swing.JToggleButton
import javax.swing.ListSelectionModel
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
import javax.swing.event.PopupMenuEvent

internal abstract class FiskeoyePanel : SimpleToolWindowPanel(true, true), DumbAware {

    private val logger: Logger = Logger.getInstance(FiskeoyePanel::class.java)

    protected fun buildToolbar(place: String, actionGroup: ActionGroup, horizontal: Boolean = false): ActionToolbar {
        return ActionManager.getInstance().createActionToolbar(place, actionGroup, horizontal).apply {
            setShowSeparatorTitles(true)
        }
    }

    protected fun buildTableActionGroup(urlLabel: JBLabel, mainTable: JBTable): DefaultActionGroup {
        return DefaultActionGroup().apply {
            add(AddResultToClipboardAction(urlLabel, mainTable))
            add(OpenInBrowserAction(urlLabel, mainTable))
            add(ScrollToTopAction(mainTable))
            add(ScrollToEndAction(mainTable))
            add(FilterActionGroup(mainTable))
            add(SettingAction())
            add(HelpAction())
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
            autoscrolls = true
            autoResizeMode = AUTO_RESIZE_OFF
            setDefaultRenderer(LabelIcon::class.java, LabelIconRenderer())
            setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION)
            setShowGrid(false)
            setDefaultEditor(Object::class.java, null)
            addMouseListener(TableCellMouseListener())
            addKeyListener(TableCellKeyListener())
            putClientProperty("terminateEditOnFocusLost", true)
            resetDefaultFocusTraversalKeys()
        }
    }

    protected fun buildScrollPane(table: JBTable): JBScrollPane {
        return JBScrollPane(table).apply {
            verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED
        }
    }

    protected fun buildFont(): Font {
        val state = FiskeoyeState().state
        return Font(state.fontName, state.fontStyle.index, state.fontSize)
    }

    private fun buildPopupMenu(table: JBTable): JBPopupMenu {
        return JBPopupMenu().apply popupMenu@{
            this.addPopupMenuListener(object : PopupMenuListenerAdapter() {
                override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
                    removeAll()
                    val isValid = try {
                        val isNotEmpty = !table.isEmpty
                        val hasData = table.selectedRow >= 0 && table.selectedRow < table.rowCount
                        val isNotNull = table.selectedRow != -1 && table.model.getValueAt(table.convertRowIndexToModel(table.selectedRow), 1) != null
                        isNotNull && hasData && isNotEmpty
                    } catch (e: Exception) {
                        logger.error(e)
                        false
                    }
                    if (!isValid) {
                        add(JBMenuItem("Nothing here"))
                        return
                    }
                    add(CopyTextMenuItem(table))
                    add(CopyLinkMenuItem(table))
                    add(CopyLinkForMarkdownMenuItem(table))
                    add(CopyLinkForJiraMenuItem(table))
                }
            })
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

    @Suppress("UnstableApiUsage")
    protected fun buildSearchButton(fiskeoyeActionListener: FiskeoyeActionListener): JButton {
        return JButton().apply {
            defaultButton()
            text = "Search"
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