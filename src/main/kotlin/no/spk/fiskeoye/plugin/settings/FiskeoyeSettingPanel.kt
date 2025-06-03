package no.spk.fiskeoye.plugin.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import no.spk.fiskeoye.plugin.enum.FontStyle
import java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment
import javax.swing.JPanel

@Suppress("UNCHECKED_CAST", "JoinDeclarationAndAssignment")
internal class FiskeoyeSettingPanel : FiskeoyeBaseSettingPanel() {

    // Appearance
    private val fontNameCombobox: ComboBox<String>
    private val fontStyleComboBox: ComboBox<String>
    private val fontSizeComboBox: ComboBox<Int>

    // Configuration
    private val baseUrlTextField: JBTextField
    private val truncSizeComboBox: ComboBox<Int>
    private val codeLengthComboBox: ComboBox<Int>

    init {
        // Appearance
        this.fontNameCombobox = buildComboBox(getLocalGraphicsEnvironment().availableFontFamilyNames.toList()) as ComboBox<String>
        this.fontStyleComboBox = buildComboBox(FontStyle.entries.map { it.name }.toList()) as ComboBox<String>
        this.fontSizeComboBox = buildComboBox((8..26).toList()) as ComboBox<Int>
        this.add(buildFontPanel(), buildGridBagConstraints(0, 0.0))

        // Configuration
        this.baseUrlTextField = JBTextField(50)
        this.truncSizeComboBox = buildComboBox(listOf(1000, 2000, 3000, 4000, 5000)) as ComboBox<Int>
        this.codeLengthComboBox = buildComboBox(listOf(200, 300, 400, 500)) as ComboBox<Int>
        this.add(buildConfigurationPanel(), buildGridBagConstraints(1, 1.0))
    }

    private fun buildFontPanel(): JPanel = FormBuilder.createFormBuilder()
        .setAlignLabelOnRight(false)
        .addLabeledComponent(JBLabel("Font:"), fontNameCombobox, 1, false)
        .addLabeledComponent(JBLabel("Style:"), fontStyleComboBox, 1, false)
        .addLabeledComponent(JBLabel("Size:"), fontSizeComboBox, 1, false)
        .panel.apply {
            border = IdeBorderFactory.createTitledBorder("Appearance")
        }

    private fun buildConfigurationPanel(): JPanel = FormBuilder.createFormBuilder()
        .setAlignLabelOnRight(false)
        .addLabeledComponent(JBLabel("Base-url:"), baseUrlTextField, 1, false)
        .addLabeledComponent(JBLabel("Trunc-size:"), truncSizeComboBox, 1, false)
        .addLabeledComponent(JBLabel("Code-length:"), codeLengthComboBox, 1, false)
        .panel.apply {
            border = IdeBorderFactory.createTitledBorder("Configuration")
        }

    fun isModified(state: FiskeoyeState): Boolean {
        return listOf(
            fontNameCombobox.selectedItem != state.fontName,
            fontStyleComboBox.selectedItem != state.fontStyle.name,
            fontSizeComboBox.selectedItem != state.fontSize,
            baseUrlTextField.text != state.baseUrl,
            truncSizeComboBox.selectedItem != state.truncSize,
            codeLengthComboBox.selectedItem != state.codeLength
        ).any { it }
    }

    fun apply(state: FiskeoyeState) {
        state.fontName = fontNameCombobox.model.selectedItem as String
        state.fontStyle = FontStyle.valueOf(fontStyleComboBox.model.selectedItem as String)
        state.fontSize = fontSizeComboBox.model.selectedItem as Int
        state.baseUrl = baseUrlTextField.text
        state.truncSize = truncSizeComboBox.model.selectedItem as Int
        state.codeLength = codeLengthComboBox.model.selectedItem as Int
    }

    fun reset(state: FiskeoyeState) {
        fontNameCombobox.model.selectedItem = state.fontName
        fontStyleComboBox.selectedItem = state.fontStyle.name
        fontSizeComboBox.selectedItem = state.fontSize
        baseUrlTextField.text = state.baseUrl
        truncSizeComboBox.selectedItem = state.truncSize
        codeLengthComboBox.selectedItem = state.codeLength
    }

}
