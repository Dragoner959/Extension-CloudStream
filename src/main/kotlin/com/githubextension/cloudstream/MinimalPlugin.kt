package com.githubextension.cloudstream

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

class EmptyAPI : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "Empty API"
    override var lang = "en"
    override val hasMainPage = true
    override val supportedTypes = setOf(com.lagradost.cloudstream3.TvType.Movie)
}

@CloudstreamPlugin
class MinimalPlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(EmptyAPI())
    }
}
