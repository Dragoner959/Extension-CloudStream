package com.githubextension.cloudstream

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class NhentaiAPI : MainAPI() {
    override var mainUrl = "https://nhentai.net"
    override var name = "Nhentai"
    override var lang = "es"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    private val apiBase = "https://nhentai.net/api/v2"
    private val apiKey = "nhk_YdRbF8dIcG4DXqxhO2N1EPRrBryqvHrRVJWL_xHTwAGOSCQ4"

    private val headers = mapOf(
        "Authorization" to "Key $apiKey",
        "User-Agent" to "CloudStreamPlugin/1.0 (https://github.com/tuusuario/tu-repo)"
    )

    data class GalleryListResponse(
        @JsonProperty("result") val result: List<GalleryItem>? = null
    )

    data class GalleryItem(
        @JsonProperty("id") val id: Int? = null,
        @JsonProperty("title") val title: TitleInfo? = null,
        @JsonProperty("media_id") val mediaId: String? = null,
        @JsonProperty("images") val images: ImagesInfo? = null,
        @JsonProperty("cover") val cover: String? = null
    )

    data class TitleInfo(
        @JsonProperty("english") val english: String? = null,
        @JsonProperty("japanese") val japanese: String? = null,
        @JsonProperty("pretty") val pretty: String? = null
    )

    data class ImagesInfo(
        @JsonProperty("cover") val cover: ImageData? = null,
        @JsonProperty("pages") val pages: List<ImageData>? = null
    )

    data class ImageData(
        @JsonProperty("t") val type: String? = null,
        @JsonProperty("w") val width: Int? = null,
        @JsonProperty("h") val height: Int? = null,
        @JsonProperty("name") val name: String? = null,
        @JsonProperty("src") val src: String? = null
    )

    data class GalleryDetailResponse(
        @JsonProperty("id") val id: Int? = null,
        @JsonProperty("title") val title: TitleInfo? = null,
        @JsonProperty("media_id") val mediaId: String? = null,
        @JsonProperty("images") val images: ImagesInfo? = null
    )

    private val mapper = jacksonObjectMapper().registerKotlinModule()

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = "$apiBase/galleries?limit=10&page=$page"
        val res = app.get(url, headers = headers).text
        val data: GalleryListResponse = mapper.readValue(res)

        val items = data.result?.mapNotNull { item ->
            val title = item.title?.english ?: item.title?.pretty ?: "Sin título"
            val id = item.id ?: return@mapNotNull null
            newMovieSearchResponse(title, id.toString()) {
                this.posterUrl = buildCoverUrl(item.mediaId, item.images?.cover?.name)
            }
        } ?: emptyList()

        return newHomePageResponse(listOf(HomePageList("Popular", items)), items.isNotEmpty())
    }

    override suspend fun search(query: String): List<SearchResponse> {
        if (query.isBlank()) return emptyList()
        val url = "$apiBase/search?q=${query.trim()}"
        val res = app.get(url, headers = headers).text
        val data: GalleryListResponse = mapper.readValue(res)

        return data.result?.mapNotNull { item ->
            val title = item.title?.english ?: item.title?.pretty ?: "Sin título"
            val id = item.id ?: return@mapNotNull null
            newMovieSearchResponse(title, id.toString()) {
                this.posterUrl = buildCoverUrl(item.mediaId, item.images?.cover?.name)
            }
        } ?: emptyList()
    }

    override suspend fun load(url: String): LoadResponse {
        val id = url.toIntOrNull() ?: return newMovieLoadResponse("Sin título", url, TvType.Movie, url) {}
        val res = app.get("$apiBase/galleries/$id", headers = headers).text
        val detail: GalleryDetailResponse = mapper.readValue(res)
        val title = detail.title?.english ?: detail.title?.pretty ?: "Sin título"

        return newMovieLoadResponse(title, id.toString(), TvType.Movie, id.toString()) {
            this.posterUrl = buildCoverUrl(detail.mediaId, detail.images?.cover?.name)
            this.plot = title
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val id = data.toIntOrNull() ?: return false
        val res = app.get("$apiBase/galleries/$id", headers = headers).text
        val detail: GalleryDetailResponse = mapper.readValue(res)

        val cdnBase = getCdnBase()
        detail.images?.pages?.forEachIndexed { index, page ->
            val imageUrl = when {
                !page.src.isNullOrBlank() -> page.src
                !detail.mediaId.isNullOrBlank() -> "$cdnBase/${detail.mediaId}/${index + 1}${page.name ?: ""}"
                else -> null
            }
            if (imageUrl.isNullOrBlank()) return@forEachIndexed
            callback.invoke(
                newExtractorLink(
                    source = name,
                    name = "$name ${index + 1}",
                    url = imageUrl,
                    type = ExtractorLinkType.VIDEO
                ) {
                    this.referer = mainUrl
                    this.quality = Qualities.Unknown.value
                }
            )
        }
        return true
    }

    private fun buildCoverUrl(mediaId: String?, fileName: String?): String? {
        if (mediaId.isNullOrBlank()) return null
        val ext = fileName?.substringAfterLast('.', missingDelimiterValue = "")?.takeIf { it.isNotBlank() }
        return if (ext.isNullOrBlank()) {
            "https://t.nhentai.net/galleries/$mediaId/cover.jpg"
        } else {
            "https://t.nhentai.net/galleries/$mediaId/cover.$ext"
        }
    }

    private suspend fun getCdnBase(): String {
        val config = app.get("$apiBase/cdn", headers = headers).text
        return Regex("\"base\":\\s*\"([^\"]+)\"").find(config)?.groupValues?.get(1) ?: "https://i.nhentai.net"
    }
}
