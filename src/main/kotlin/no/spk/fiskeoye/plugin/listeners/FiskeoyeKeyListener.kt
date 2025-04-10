package no.spk.fiskeoye.plugin.listeners

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

internal abstract class FiskeoyeKeyListener : KeyListener {

    override fun keyTyped(e: KeyEvent) = Unit

    override fun keyReleased(e: KeyEvent) = Unit

}
