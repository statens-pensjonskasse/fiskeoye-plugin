package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import javax.swing.JComponent

internal class CustomFiskeoyeAction(private val jComponent: JComponent) : FiskeoyeAction(), CustomComponentAction {

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent = jComponent

    override fun actionPerformed(e: AnActionEvent) = Unit

}
