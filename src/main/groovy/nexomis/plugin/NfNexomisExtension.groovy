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
    
    /**
     * Display help information for all schema files found in the project.
     * This function discovers schema files referenced in nextflow_schema.json
     * and displays formatted help for each one.
     */
    @Function
    void showAssetsHelp() {
        def assetsHelper = new AssetsHelper(this.session.baseDir)
        
        // Discover schema files from nextflow_schema.json
        def schemaFiles = assetsHelper.discoverSchemaFiles()
        
  
        // Process discovered schema files
        println "\nFound ${schemaFiles.size()} schema file(s) in nextflow_schema.json:"
        schemaFiles.each { schemaPath ->
            println "Processing: ${schemaPath}"
            def helpOutput = assetsHelper.generateSchemaHelp(schemaPath)
            if (helpOutput) {
                println helpOutput
            }
        }
        
        // Add footer
        def ANSI_GREY = "\u001B[90m"
        def ANSI_RESET = "\u001B[0m"
        println "-${ANSI_GREY}----------------------------------------------------${ANSI_RESET}-"
    }

}
