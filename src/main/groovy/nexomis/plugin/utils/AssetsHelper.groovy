package nexomis.plugin.utils

import groovy.json.JsonSlurper
import java.nio.file.Path
import java.nio.file.Files

/**
 * Helper class for processing and displaying schema help information
 * 
 * @author: nexomis team
 */
class AssetsHelper {

    private Path baseDir
    
    AssetsHelper(Path baseDir) {
        this.baseDir = baseDir
    }

    /**
     * Generate help message for a specific schema file
     */
    String generateSchemaHelp(String schemaPath) {
        def schemaFile = new File("${baseDir}/${schemaPath}")
        
        if (!schemaFile.exists()) {
            println "Warning: Schema file not found: ${schemaPath}"
            return ""
        }

        try {
            def jsonSlurper = new JsonSlurper()
            def schema = jsonSlurper.parse(schemaFile)
            return formatSchemaHelp(schema, schemaPath)
        } catch (Exception e) {
            println "Error parsing schema file ${schemaPath}: ${e.message}"
            return ""
        }
    }

    /**
     * Format schema help output with ANSI styling
     */
    private String formatSchemaHelp(Map schema, String schemaPath) {
        // ANSI escape codes for styling
        def ANSI_BOLD = "\u001B[1m"
        def ANSI_UNDERLINE = "\u001B[4m"
        def ANSI_RESET = "\u001B[0m"
        def ANSI_GREY = "\u001B[90m"
        def ANSI_BLUE = "\u001B[34m"

        def title = schema['title'] ?: "Schema: ${schemaPath}"
        def description = schema['description'] ?: ''

        def output = "\n"

        // Add Title and Description
        output += "${ANSI_BOLD}${ANSI_UNDERLINE}${title}${ANSI_RESET}\n"
        if (description) {
            output += "${description}\n\n"
        }

        // Check if this is an items-based schema (for arrays/lists)
        def properties = schema.items?.properties ?: schema.properties
        def requiredFields = schema.items?.required ?: schema.required ?: []

        if (!properties) {
            output += "No properties found in schema.\n"
            return output
        }

        def allColumns = []

        properties.each { key, value ->
            def column = [:]
            column.key = key
            column.type = extractType(value)
            column.description = value['description'] ?: ''
            column.defaultValue = value['default'] ?: ''
            column.required = requiredFields.contains(key)
            allColumns << column
        }

        if (allColumns.isEmpty()) {
            output += "No columns defined in schema.\n"
            return output
        }

        def maxKeyLength = allColumns.collect { it.key.size() }.max() ?: 10

        // Required columns section
        def requiredColumns = allColumns.findAll { it.required }
        if (requiredColumns) {
            output += "${ANSI_UNDERLINE}Required columns:${ANSI_RESET}\n"
            requiredColumns.each { col ->
                def typeStr = "${ANSI_GREY}[${col.type}]${ANSI_RESET}"
                def desc = col.description
                if (col.defaultValue) {
                    desc += " ${ANSI_GREY}[default: ${col.defaultValue}]${ANSI_RESET}"
                }
                output += String.format("  ${ANSI_BLUE}%-${maxKeyLength}s${ANSI_RESET}  %s  %s\n", col.key, typeStr, desc)
            }
            output += "\n"
        }

        // Optional columns section
        def optionalColumns = allColumns.findAll { !it.required }
        if (optionalColumns) {
            output += "${ANSI_UNDERLINE}Optional columns:${ANSI_RESET}\n"
            optionalColumns.each { col ->
                def typeStr = "${ANSI_GREY}[${col.type}]${ANSI_RESET}"
                def desc = col.description
                if (col.defaultValue) {
                    desc += " ${ANSI_GREY}[default: ${col.defaultValue}]${ANSI_RESET}"
                }
                output += String.format("  ${ANSI_BLUE}%-${maxKeyLength}s${ANSI_RESET}  %s  %s\n", col.key, typeStr, desc)
            }
        }

        return output
    }

    /**
     * Extract type information from schema property
     */
    private String extractType(Map value) {
        def types = []

        if (value['type']) {
            types << value['type']
        } else if (value['format']) {
            types << value['format']
        }

        if (value['anyOf']) {
            value['anyOf'].each { subValue ->
                if (subValue['type']) {
                    types << subValue['type']
                } else if (subValue['format']) {
                    types << subValue['format']
                }
            }
        }

        if (value['oneOf']) {
            value['oneOf'].each { subValue ->
                if (subValue['type']) {
                    types << subValue['type']
                } else if (subValue['format']) {
                    types << subValue['format']
                }
            }
        }

        types = types.unique()
        def typeStr = types.join('/')

        return typeStr ?: 'unknown'
    }

    /**
     * Discover schema files from nextflow_schema.json
     */
    List<String> discoverSchemaFiles() {
        def schemaFiles = []
        def nextflowSchemaFile = new File("${baseDir}/nextflow_schema.json")
        
        if (!nextflowSchemaFile.exists()) {
            println "Info: No nextflow_schema.json found in project directory"
            return schemaFiles
        }

        try {
            def jsonSlurper = new JsonSlurper()
            def nextflowSchema = jsonSlurper.parse(nextflowSchemaFile)
            
            // Recursively search for schema properties
            schemaFiles.addAll(findSchemaReferences(nextflowSchema))
            
        } catch (Exception e) {
            println "Error parsing nextflow_schema.json: ${e.message}"
        }

        return schemaFiles.unique()
    }

    /**
     * Recursively find schema references in the nextflow schema
     */
    private List<String> findSchemaReferences(Object obj) {
        def schemaRefs = []
        
        if (obj instanceof Map) {
            obj.each { key, value ->
                if (key == 'schema' && value instanceof String) {
                    schemaRefs << value
                } else {
                    schemaRefs.addAll(findSchemaReferences(value))
                }
            }
        } else if (obj instanceof List) {
            obj.each { item ->
                schemaRefs.addAll(findSchemaReferences(item))
            }
        }
        
        return schemaRefs
    }
}
