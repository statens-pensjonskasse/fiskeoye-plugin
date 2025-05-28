package no.spk.fiskeoye.plugin.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBInsets
import no.spk.fiskeoye.plugin.enum.FontStyle
import java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment
import java.awt.GridBagConstraints

@Suppress("UNCHECKED_CAST", "JoinDeclarationAndAssignment")
internal class FiskeoyeSettingPanel : FiskeoyeBaseSettingPanel() {

    // Appearance
    private val fontNameCbox: ComboBox<String>
    private val fontStyleCbox: ComboBox<String>
    private val fontSizeCbox: ComboBox<Int>

    // Configuration
    private val baseUrlTextField: JBTextField
    private val truncSizeCbox: ComboBox<Int>
    private val codeLengthCbox: ComboBox<Int>

    init {
        // Appearance
        this.fontNameCbox = buildComboBox(getLocalGraphicsEnvironment().availableFontFamilyNames.toList()) as ComboBox<String>
        this.fontStyleCbox = buildComboBox(FontStyle.entries.map { it.name }.toList()) as ComboBox<String>
        this.fontSizeCbox = buildComboBox((8..26).toList()) as ComboBox<Int>
        val fontPanel = FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .addLabeledComponent(JBLabel("Font:"), fontNameCbox, 1, false)
            .addLabeledComponent(JBLabel("Style:"), fontStyleCbox, 1, false)
            .addLabeledComponent(JBLabel("Size:"), fontSizeCbox, 1, false)
            .panel.apply {
                border = IdeBorderFactory.createTitledBorder("Appearance")
            }
        this.add(
            fontPanel, GridBagConstraints(
                0, 0,
                1, 1,
                1.0, 0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                JBInsets(0, 0, 0, 0),
                0, 0
            )
        )

        // Configuration
        this.baseUrlTextField = JBTextField().apply {
            columns = 50
        }
        this.truncSizeCbox = buildComboBox(listOf(1000, 2000, 3000, 4000, 5000)) as ComboBox<Int>
        this.codeLengthCbox = buildComboBox(listOf(200, 300, 400, 500)) as ComboBox<Int>

        val configPanel = FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(false)
            .addLabeledComponent(JBLabel("Base-url:"), baseUrlTextField, 1, false)
            .addLabeledComponent(JBLabel("Trunc-size:"), truncSizeCbox, 1, false)
            .addLabeledComponent(JBLabel("Code-length:"), codeLengthCbox, 1, false)
            .panel.apply {
                border = IdeBorderFactory.createTitledBorder("Configuration")
            }

        this.add(
            configPanel, GridBagConstraints(
                0, 1,
                1, 1,
                1.0, 1.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                JBInsets(0, 0, 0, 0),
                0, 0
            )
        )
    }

    fun isModified(state: FiskeoyeState): Boolean {
        // Appearance
        val fontNameIsEqual = (fontNameCbox.model.selectedItem != state.fontName)
        val fontStyleIsEqual = (fontStyleCbox.model.selectedItem != state.fontStyle.name)
        val fontSizeIsEqual = (fontSizeCbox.model.selectedItem != state.fontSize)
        // Configuration
        val baseUrlIsEqual = (baseUrlTextField.text != state.baseUrl)
        val truncSizeIsEqual = (truncSizeCbox.selectedItem != state.truncSize)
        val codeLengthIsEqual = (codeLengthCbox.selectedItem != state.codeLength)
        return (fontNameIsEqual || fontStyleIsEqual || fontSizeIsEqual || baseUrlIsEqual || truncSizeIsEqual || codeLengthIsEqual)
    }

    fun apply(state: FiskeoyeState) {
        state.fontName = fontNameCbox.model.selectedItem as String
        state.fontStyle = FontStyle.valueOf(fontStyleCbox.model.selectedItem.toString())
        state.fontSize = fontSizeCbox.model.selectedItem as Int
        state.baseUrl = baseUrlTextField.text
        state.truncSize = truncSizeCbox.model.selectedItem as Int
        state.codeLength = codeLengthCbox.model.selectedItem as Int
    }

    fun reset(state: FiskeoyeState) {
        fontNameCbox.model.selectedItem = state.fontName
        fontStyleCbox.selectedItem = state.fontStyle.name
        fontSizeCbox.selectedItem = state.fontSize
        baseUrlTextField.text = state.baseUrl
        truncSizeCbox.selectedItem = state.truncSize
        codeLengthCbox.selectedItem = state.codeLength
    }

}
