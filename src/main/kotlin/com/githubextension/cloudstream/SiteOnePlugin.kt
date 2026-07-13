package com.githubextension.cloudstream

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.TvType

class SiteOneAPI : MainAPI() {
    override var mainUrl = "https://example.com/site1"
    override var name = "Site One"
    override var lang = "en"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)
}

@CloudstreamPlugin
class SiteOnePlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(SiteOneAPI())
    }
}
