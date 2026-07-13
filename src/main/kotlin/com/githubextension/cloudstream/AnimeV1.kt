package com.githubextension.cloudstream

import com.lagradost.cloudstream3.DubStatus
import com.lagradost.cloudstream3.Episode
import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.HomePageResponse
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.addDubStatus
import com.lagradost.cloudstream3.addEpisodes
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.newAnimeLoadResponse
import com.lagradost.cloudstream3.newAnimeSearchResponse
import com.lagradost.cloudstream3.newEpisode
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor

class AnimeV1 : MainAPI() {
    override var mainUrl = "https://animev1.com"
    override var name = "AnimeV1"
    override var lang = "es"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Anime)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("$mainUrl/").document
        val animes = document.select("div.anime-item").mapNotNull { element ->
            val title = element.selectFirst("a.title")?.text() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = element.selectFirst("img")?.attr("src")

            newAnimeSearchResponse(title, href) {
                this.posterUrl = poster
                addDubStatus(isDub = false)
            }
        }

        return newHomePageResponse(
            listOf(
                HomePageList("Últimos Animes", animes, isHorizontalImages = true)
            )
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document
        return document.select("div.anime-item").mapNotNull { element ->
            val title = element.selectFirst("a.title")?.text() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = element.selectFirst("img")?.attr("src")

            newAnimeSearchResponse(title, href) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1.title")?.text() ?: "Sin título"
        val poster = document.selectFirst("div.poster img")?.attr("src")
        val description = document.selectFirst("div.sinopsis")?.text()

        val episodes = document.select("ul.episodes-list li a").mapNotNull { epElement ->
            val epHref = epElement.attr("href")
            val epName = epElement.text()
            newEpisode(epHref) {
                this.name = epName
            }
        }

        val finalEpisodes = if (episodes.isEmpty()) {
            listOf(newEpisode(url) { this.name = "Episodio 1" })
        } else {
            episodes
        }

        return newAnimeLoadResponse(title, url, TvType.Anime, false) {
            this.posterUrl = poster
            this.plot = description
            if (finalEpisodes.isNotEmpty()) {
                addEpisodes(DubStatus.Subbed, finalEpisodes)
            }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        val iframes = document.select("iframe")

        for (iframe in iframes) {
            val src = iframe.attr("src")
            if (src.isNotBlank()) {
                loadExtractor(src, mainUrl, subtitleCallback, callback)
            }
        }

        return true
    }
}
