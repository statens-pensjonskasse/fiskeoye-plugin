package no.spk.fiskeoye.plugin.listeners.button

import no.spk.fiskeoye.plugin.listeners.FiskeoyeKeyListener
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent

internal abstract class FiskeoyeActionListener : FiskeoyeKeyListener(), ActionListener {

    abstract fun operation()

    override fun actionPerformed(e: ActionEvent?) = operation()

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_ENTER) {
            operation()
        }
    }

}
