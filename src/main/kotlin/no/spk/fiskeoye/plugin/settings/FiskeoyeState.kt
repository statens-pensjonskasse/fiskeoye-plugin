package no.spk.fiskeoye.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import no.spk.fiskeoye.plugin.enum.FontStyle

@State(
    name = "no.spk.fiskeoye.plugin.settings.FiskeoyeState",
    storages = [Storage("fiskeoye.xml")]
)
internal class FiskeoyeState : PersistentStateComponent<FiskeoyeState> {

    // Appearance
    internal var fontName: String = "Jetbrains Mono"
    internal var fontStyle: FontStyle = FontStyle.PLAIN
    internal var fontSize: Int = 13

    // Configuration
    internal var baseUrl: String = ""
    internal var truncSize: Int = 3000
    internal var codeLength: Int = 400

    override fun getState(): FiskeoyeState = ApplicationManager.getApplication().getService(FiskeoyeState::class.java)!!

    override fun loadState(state: FiskeoyeState) = XmlSerializerUtil.copyBean(state, this)

}
