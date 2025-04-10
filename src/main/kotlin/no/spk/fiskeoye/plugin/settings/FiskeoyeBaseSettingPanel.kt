package no.spk.fiskeoye.plugin.settings

import com.intellij.openapi.ui.ComboBox
import java.awt.GridBagLayout
import javax.swing.JPanel


internal abstract class FiskeoyeBaseSettingPanel : JPanel(GridBagLayout()) {

    protected fun buildComboBox(values: List<Any>): ComboBox<Any> {
        return ComboBox<Any>().apply {
            values.forEach { item -> addItem(item) }
        }
    }

}
