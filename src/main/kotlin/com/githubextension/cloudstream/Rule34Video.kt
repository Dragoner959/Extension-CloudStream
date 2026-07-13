package com.githubextension.cloudstream

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class Rule34Video : MainAPI() {
    override var mainUrl = "https://rule34video.com"
    override var name = "Rule34Video"
    override var lang = "es"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get("$mainUrl/latest/?page=$page").document
        val items = doc.select("div.thumb").mapNotNull { it.toSearchResult() }
        return newHomePageResponse(listOf(HomePageList("Últimos", items)), items.isNotEmpty())
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/?q=$query").document
        return doc.select("div.thumb").mapNotNull { it.toSearchResult() }
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val aTag = selectFirst("a") ?: return null
        val href = aTag.attr("href").let { if (it.startsWith("http")) it else "$mainUrl$it" }
        val title = aTag.attr("title").ifBlank { aTag.text() }
        val poster = selectFirst("img")?.attr("src")
        return newMovieSearchResponse(title, href) { this.posterUrl = poster }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text() ?: "Sin título"
        val poster = doc.selectFirst("video")?.attr("poster") ?: doc.selectFirst("meta[property=og:image]")?.attr("content")

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = doc.selectFirst("meta[name=description]")?.attr("content")
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document

        val sourceUrl = doc.selectFirst("video source")?.attr("src")
        if (!sourceUrl.isNullOrBlank()) {
            val isM3u8 = sourceUrl.contains(".m3u8")
            callback.invoke(
                newExtractorLink(
                    source = name,
                    name = name,
                    url = sourceUrl,
                    type = if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO
                ) {
                    this.referer = mainUrl
                    this.quality = if (isM3u8) Qualities.Unknown.value else Qualities.P1080.value
                }
            )
        }

        val iframes = doc.select("iframe").map { it.attr("src") }
        iframes.forEach { iframeUrl ->
            if (iframeUrl.isNotBlank()) {
                loadExtractor(iframeUrl, mainUrl, subtitleCallback, callback)
            }
        }

        return true
    }
}
