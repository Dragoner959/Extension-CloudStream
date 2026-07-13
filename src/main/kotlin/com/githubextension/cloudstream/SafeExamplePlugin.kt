package com.githubextension.cloudstream

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.utils.*

class EpornerAPI : MainAPI() {
    private val mapper = jacksonObjectMapper().registerKotlinModule()
    override var mainUrl = "https://www.eporner.com"
    override var name = "Eporner API"
    override var lang = "es"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    private val apiUrl = "https://www.eporner.com/api/v2"

    // Data Classes para el JSON de Eporner (Mucho mejor que Regex)
    data class EpornerSearchResponse(@JsonProperty("videos") val videos: List<EpornerVideo>?)
    data class EpornerVideo(
        @JsonProperty("id") val id: String?,
        @JsonProperty("title") val title: String?,
        @JsonProperty("thumbnails") val thumbnails: List<Thumb>?
    )
    data class Thumb(@JsonProperty("src") val src: String?)
    
    data class EpornerDetailResponse(
        @JsonProperty("title") val title: String?,
        @JsonProperty("id") val id: String?,
        @JsonProperty("source") val source: Source?
    )
    data class Source(@JsonProperty("mp4") val mp4: Map<String, Mp4Source>?)
    data class Mp4Source(@JsonProperty("url") val url: String?)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val res = app.get("$apiUrl/video/search/?query=all&per_page=30&page=$page&format=json").text
        val data: EpornerSearchResponse = mapper.readValue(res)
        
        val items = data.videos?.mapNotNull { vid ->
            newMovieSearchResponse(vid.title ?: "", vid.id ?: "") {
                this.posterUrl = vid.thumbnails?.firstOrNull()?.src
            }
        } ?: emptyList()

        return newHomePageResponse(listOf(HomePageList("Últimos", items)), items.isNotEmpty())
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val res = app.get("$apiUrl/video/search/?query=$query&per_page=30&format=json").text
        val data: EpornerSearchResponse = mapper.readValue(res)
        return data.videos?.mapNotNull { vid ->
            newMovieSearchResponse(vid.title ?: "", vid.id ?: "") { this.posterUrl = vid.thumbnails?.firstOrNull()?.src }
        } ?: emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        // url aquí es el ID del video (pasado desde search)
        val res = app.get("$apiUrl/video/id/?id=$url&thumbsize=medium&format=json").text
        val data: EpornerDetailResponse = mapper.readValue(res)
        
        return newMovieLoadResponse(data.title ?: "", url, TvType.Movie, url) { // Pasamos el ID de nuevo a loadLinks
            this.posterUrl = data.source?.mp4?.values?.firstOrNull()?.url
            this.plot = data.title
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        // data es el ID del video
        val res = app.get("$apiUrl/video/id/?id=$data&format=json").text
        val detail: EpornerDetailResponse = mapper.readValue(res)
        
        // La API de Eporner da los .mp4 directamente en diferentes calidades
        detail.source?.mp4?.forEach { (quality, mp4Obj) ->
            val q = when(quality) {
                "720" -> Qualities.P720.value
                "1080" -> Qualities.P1080.value
                "480" -> Qualities.P480.value
                else -> Qualities.Unknown.value
            }
            callback.invoke(
                newExtractorLink(
                    source = name,
                    name = "$name $quality",
                    url = mp4Obj.url ?: return@forEach,
                    type = ExtractorLinkType.VIDEO
                ) {
                    this.referer = mainUrl
                    this.quality = q
                }
            )
        }
        return true
    }
}

@CloudstreamPlugin
class SafeExamplePlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(EpornerAPI())
        registerMainAPI(Rule34Video())
        registerMainAPI(AnimeV1())
        registerMainAPI(NhentaiAPI())
    }
}