package no.spk.fiskeoye.plugin.settings

import com.intellij.openapi.options.Configurable
import no.spk.fiskeoye.plugin.util.getFileContentPanel
import no.spk.fiskeoye.plugin.util.getFileNamePanel
import javax.swing.JComponent

internal class FiskeoyeConfigurable : Configurable {

    private val settingPanel: FiskeoyeSettingPanel = FiskeoyeSettingPanel()

    override fun createComponent(): JComponent = settingPanel

    override fun isModified(): Boolean = settingPanel.isModified(FiskeoyeState().state)

    override fun apply() {
        settingPanel.apply(FiskeoyeState().state)
        update()
    }

    override fun reset() = settingPanel.reset(FiskeoyeState().state)

    override fun getDisplayName(): String = "Fiskeoye"

    private fun update() {
        getFileContentPanel().update()
        getFileNamePanel().update()
    }

}
