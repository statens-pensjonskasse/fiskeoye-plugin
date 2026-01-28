package no.spk.fiskeoye.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.spk.fiskeoye.plugin.util.handleSpecialChar

internal class FiskeoyeUtilTest : DescribeSpec({

    it("String.handleSpecialChar()") {
        "(test)".handleSpecialChar() shouldBe "[(]test[)]"
        "((test))".handleSpecialChar() shouldBe "[(][(]test[)][)]"
        ".test.".handleSpecialChar() shouldBe "[.]test[.]"
        "..test..".handleSpecialChar() shouldBe "[.][.]test[.][.]"
    }

})