package nexomis.plugin

import groovy.transform.CompileStatic
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper

/**
 * The plugin entry point
 */
@CompileStatic
class NfNexomisPlugin extends BasePlugin {

    NfNexomisPlugin(PluginWrapper wrapper) {
        super(wrapper)
    }
}
