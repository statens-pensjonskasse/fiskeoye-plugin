package no.spk.fiskeoye.plugin.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import javax.swing.Icon

internal abstract class FiskeoyeAction(
    open val toolTip: String? = null,
    open val icon: Icon? = null
) : AnAction(toolTip, null, icon) {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

}