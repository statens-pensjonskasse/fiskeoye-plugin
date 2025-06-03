package no.spk.fiskeoye.plugin.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.JBInsets
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel


internal abstract class FiskeoyeBaseSettingPanel : JPanel(GridBagLayout()) {

    protected fun buildComboBox(values: List<Any>): ComboBox<Any> {
        return ComboBox<Any>().apply {
            values.forEach { item -> addItem(item) }
        }
    }

    protected fun buildGridBagConstraints(gridY: Int, weighty: Double): GridBagConstraints {
        return GridBagConstraints(
            0, gridY,
            1, 1,
            1.0, weighty,
            GridBagConstraints.NORTHWEST,
            GridBagConstraints.HORIZONTAL,
            JBInsets(0, 0, 0, 0),
            0, 0
        )
    }

}
