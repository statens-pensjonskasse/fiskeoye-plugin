package no.spk.fiskeoye.plugin.actions.window

import com.intellij.openapi.actionSystem.AnActionEvent
import no.spk.fiskeoye.plugin.actions.FiskeoyeAction
import no.spk.fiskeoye.plugin.icons.FiskeoyeIcons.Help
import no.spk.fiskeoye.plugin.util.makeUrl
import no.spk.fiskeoye.plugin.util.openUrlWithBrowser

internal class HelpAction() : FiskeoyeAction("Help", Help) {

    override fun actionPerformed(p0: AnActionEvent) {
        val usageUrl = makeUrl("https://github.com/statens-pensjonskasse/fiskeoye-plugin?tab=readme-ov-file#usage") ?: return
        openUrlWithBrowser(usageUrl)
    }

}