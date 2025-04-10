package no.spk.fiskeoye.plugin.listeners.button

import no.spk.fiskeoye.plugin.ui.FileContentPanel
import no.spk.fiskeoye.plugin.util.clear

internal class FileContentClearListener(private val fileContentPanel: FileContentPanel) : FiskeoyeActionListener() {

    override fun operation() {
        fileContentPanel.apply {
            includeField.text = ""
            excludeButton.isSelected = false
            excludeField.text = ""
            caseSensitiveButton.isSelected = false
            sortByCombobox.selectedIndex = 0
            mainTable.clear()
            includeField.requestFocus()
        }
    }

}
