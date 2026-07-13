package com.githubextension.cloudstream

import com.lagradost.cloudstream3.plugins.BasePlugin
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin

@CloudstreamPlugin
class MinimalPlugin : BasePlugin() {
    override fun load() {
        // Minimal plugin entrypoint so Cloudstream can load the package.
    }
}
