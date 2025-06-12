package nexomis.plugin

import groovy.transform.CompileStatic
import nextflow.Session
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint
import nexomis.plugin.utils.AssetsHelper

/**
 * Implements a custom function which can be imported by
 * Nextflow scripts.
 */
@CompileStatic
class NfNexomisExtension extends PluginExtensionPoint {

    private Session session

    @Override
    protected void init(Session session) {
        this.session = session
    }

}
