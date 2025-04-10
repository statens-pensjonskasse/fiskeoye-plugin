package no.spk.fiskeoye.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.spk.fiskeoye.plugin.util.getHeaderText

internal class FilenameGetHeaderTest : DescribeSpec({

    it("getHeader() with include.isEmpty(): true") {
        val include = ""
        val isCaseSensitive = false
        val isSearchInFullPath = false
        getHeaderText(include, isCaseSensitive, isSearchInFullPath) shouldBe ""
    }

    it("getHeader() with isCaseSensitive: true, isSearchInFullPath: true") {
        val include = "TEST"
        val isCaseSensitive = true
        val isSearchInFullPath = true
        getHeaderText(
            include,
            isCaseSensitive,
            isSearchInFullPath
        ) shouldBe "Case-sensitive Søkeresultat for filer med '$include' i navnet eller i filstien."
    }

    it("getHeader() with isCaseSensitive: true, isSearchInFullPath: false") {
        val include = "TEST"
        val isCaseSensitive = true
        val isSearchInFullPath = false
        getHeaderText(
            include,
            isCaseSensitive,
            isSearchInFullPath
        ) shouldBe "Case-sensitive Søkeresultat for filer med '$include' i navnet."
    }

    it("getHeader() with isCaseSensitive: false, isSearchInFullPath: false") {
        val include = "TEST"
        val isCaseSensitive = false
        val isSearchInFullPath = false
        getHeaderText(
            include,
            isCaseSensitive,
            isSearchInFullPath
        ) shouldBe "Søkeresultat for filer med '$include' i navnet."
    }

    it("getHeader() with isCaseSensitive: false, isSearchInFullPath: true") {
        val include = "TEST"
        val isCaseSensitive = false
        val isSearchInFullPath = true
        getHeaderText(
            include,
            isCaseSensitive,
            isSearchInFullPath
        ) shouldBe "Søkeresultat for filer med '$include' i navnet eller i filstien."
    }

})