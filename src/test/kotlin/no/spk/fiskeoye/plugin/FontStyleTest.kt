package no.spk.fiskeoye.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.spk.fiskeoye.plugin.enum.FontStyle

internal class FontStyleTest : DescribeSpec({

    it("FontStyle.findByIndex()") {
        FontStyle.findByIndex(0) shouldBe FontStyle.PLAIN
        FontStyle.findByIndex(1) shouldBe FontStyle.BOLD
        FontStyle.findByIndex(2) shouldBe FontStyle.ITALIC
        shouldThrow<NoSuchElementException> {
            FontStyle.findByIndex(5)
        }.message shouldBe "FontStyle with index '5' not found."
    }

})