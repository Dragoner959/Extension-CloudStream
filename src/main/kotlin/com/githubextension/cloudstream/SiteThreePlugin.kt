package com.githubextension.cloudstream

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.TvType

class SiteThreeAPI : MainAPI() {
    override var mainUrl = "https://example.com/site3"
    override var name = "Site Three"
    override var lang = "en"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)
}

@CloudstreamPlugin
class SiteThreePlugin : BasePlugin() {
    override fun load() {
        registerMainAPI(SiteThreeAPI())
    }
}
