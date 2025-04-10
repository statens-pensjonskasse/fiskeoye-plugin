package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Settings
import no.spk.fiskeoye.plugin.settings.FiskeoyeConfigurable

internal class SettingAction : FiskeoyeAction("Settings", Settings) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ShowSettingsUtil.getInstance().showSettingsDialog(project, FiskeoyeConfigurable::class.java)
    }

}