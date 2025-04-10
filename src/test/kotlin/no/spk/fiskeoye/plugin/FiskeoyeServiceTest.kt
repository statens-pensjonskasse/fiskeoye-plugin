package no.spk.fiskeoye.plugin

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import no.spk.fiskeoye.plugin.enum.SortBy
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFileContent
import no.spk.fiskeoye.plugin.service.FiskeoyeService.getFilename

internal class FiskeoyeServiceTest : DescribeSpec({

    val fiskoyeUrl = "Fill inn url her"

    xit("getFileContent()") {
        val includeTest = "ResettableAccessTokenProvider"
        val isExclude = false
        val excludeText = ""
        val isCaseSensitive = false
        val sortedBy = SortBy.PROJECT

        val (requestUrl, elements) = getFileContent(includeTest, isExclude, excludeText, isCaseSensitive, sortedBy)
        requestUrl shouldContain fiskoyeUrl
        elements shouldNotBe null
    }

    xit("getFileName()") {
        val includeText = "ResettableAccessTokenProvider"
        val isCaseSensitive = false
        val isSearchInFullPath = false
        val sortedBy = SortBy.REPO

        val (requestUrl, elements) = getFilename(includeText, isCaseSensitive, isSearchInFullPath, sortedBy)
        requestUrl shouldContain fiskoyeUrl
        elements shouldNotBe null
    }

})