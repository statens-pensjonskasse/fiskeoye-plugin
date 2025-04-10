package no.spk.fiskeoye.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import no.spk.fiskeoye.plugin.util.getHeaderText

internal class FileContentGetHeaderTest : DescribeSpec({

    it("getHeader() with include.isEmpty(): true") {
        val include = ""
        val isExclude = false
        val exclude = ""
        val isCaseSensitive = false
        getHeaderText(include, isExclude, exclude, isCaseSensitive) shouldBe ""
    }

    it("getHeader() with isExclude: true, exclude.isEmpty(): true, isCaseSensitve: true") {
        val include = "TEST"
        val isExclude = true
        val exclude = ""
        val isCaseSensitive = true
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Case-sensitive Søkeresultat for 'TEST'."
    }

    it("getHeader() with isExclude: true, exclude.isEmpty(): false, isCaseSensitve: true") {
        val include = "TEST"
        val isExclude = true
        val exclude = "TEST"
        val isCaseSensitive = true
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Case-sensitive Søkeresultat for '$include' som IKKE inneholder '$exclude'."
    }

    it("getHeader() with isExclude: true, exclude.isEmpty(): true, isCaseSensitve: false") {
        val include = "TEST"
        val isExclude = true
        val exclude = ""
        val isCaseSensitive = false
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Søkeresultat for '$include'."
    }

    it("getHeader() with isExclude: true, exclude.isEmpty(): false, isCaseSensitve: false") {
        val include = "TEST"
        val isExclude = true
        val exclude = "TEST"
        val isCaseSensitive = false
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Søkeresultat for '$include' som IKKE inneholder '$exclude'."
    }

    it("getHeader() with isExclude: false, exclude.isEmpty(): false, isCaseSensitve: false") {
        val include = "TEST"
        val isExclude = false
        val exclude = "TEST"
        val isCaseSensitive = false
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Søkeresultat for '$include' som også inneholder '$exclude'."
    }

    it("getHeader() with isExclude: false, exclude.isEmpty(): true, isCaseSensitve: false") {
        val include = "TEST"
        val isExclude = false
        val exclude = ""
        val isCaseSensitive = false
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Søkeresultat for '$include'."
    }

    it("getHeader() with isExclude: false, exclude.isEmpty(): false, isCaseSensitve: true") {
        val include = "TEST"
        val isExclude = false
        val exclude = "TEST"
        val isCaseSensitive = true
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Case-sensitive Søkeresultat for '$include' som også inneholder '$exclude'."
    }

    it("getHeader() with isExclude: false, exclude.isEmpty(): true, isCaseSensitve: true") {
        val include = "TEST"
        val isExclude = false
        val exclude = ""
        val isCaseSensitive = true
        getHeaderText(
            include,
            isExclude,
            exclude,
            isCaseSensitive
        ) shouldBe "Case-sensitive Søkeresultat for '$include'."
    }

})
