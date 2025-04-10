package no.spk.fiskeoye.plugin.listeners.toggle

import no.spk.fiskeoye.plugin.listeners.FiskeoyeKeyListener
import java.awt.event.KeyEvent
import javax.swing.JToggleButton

internal class ToggleKeyListener(private val toggle: JToggleButton) : FiskeoyeKeyListener() {

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_ENTER) {
            toggle.isSelected = !toggle.isSelected
        }
    }

}
