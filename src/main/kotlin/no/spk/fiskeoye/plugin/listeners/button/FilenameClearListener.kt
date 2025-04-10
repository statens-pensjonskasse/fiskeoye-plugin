package no.spk.fiskeoye.plugin.listeners.button

import no.spk.fiskeoye.plugin.ui.FilenamePanel
import no.spk.fiskeoye.plugin.util.clear

internal class FilenameClearListener(private val filenamePanel: FilenamePanel) : FiskeoyeActionListener() {

    override fun operation() {
        filenamePanel.apply {
            includeField.text = ""
            caseSensitiveButton.isSelected = false
            searchInFullPathButton.isSelected = false
            sortByCombobox.selectedIndex = 0
            mainTable.clear()
            includeField.requestFocus()
        }
    }

}
