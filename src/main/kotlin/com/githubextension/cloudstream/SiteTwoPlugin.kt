package com.githubextension.cloudstream

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.TvType

class SiteTwoAPI : MainAPI() {
    override var mainUrl = "https://example.com/site2"
    override var name = "Site Two"
    override var lang = "en"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)
}

@CloudstreamPlugin
class SiteTwoPlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(SiteTwoAPI())
    }
}
