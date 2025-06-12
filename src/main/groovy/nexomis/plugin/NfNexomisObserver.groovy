package nexomis.plugin

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.trace.TraceObserver
import nextflow.trace.GraphObserver
import nextflow.trace.ReportObserver
import nextflow.trace.TimelineObserver
import nextflow.trace.TraceFileObserver
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Comparator

/**
 * Implements an observer that allows implementing custom
 * logic on nextflow execution events.
 */
@Slf4j
@CompileStatic
class NfNexomisObserver implements TraceObserver {

    Path baseDir
    Path outputDir

    @Override
    void onFlowCreate(Session session) {
        println """
                    #################################################
                    #    _  _                             _         #
                    #   | \\| |  ___  __ __  ___   _ __   (_)  __    #
                    #   | .` | / -_) \\ \\ / / _ \\ | '  \\  | | (_-<   #
                    #   |_|\\_| \\___| /_\\_\\ \\___/ |_|_|_| |_| /__/   #
                    #                                               #
                    #################################################
                    https://github.com/nexomis     https://nexomis.io
            """
        this.baseDir = session.baseDir
        this.outputDir = session.outputDir
    }
    
    @Override
    void onFlowComplete() {
        String[] pipeline_info = [
            GraphObserver.DEF_FILE_NAME,
            TraceFileObserver.DEF_FILE_NAME,
            ReportObserver.DEF_FILE_NAME,
            TimelineObserver.DEF_FILE_NAME
        ]
        try {
            // Define the source pipeline_info directory in baseDir

            Path dirPipelineInfo = outputDir.resolve("pipeline_info")
            // create dir
            Files.createDirectories(dirPipelineInfo)
            // copy pipeline_info to outputDir
            pipeline_info.each { filename ->
                Files.copy(baseDir.resolve(filename), dirPipelineInfo.resolve(filename), StandardCopyOption.REPLACE_EXISTING)
            }
            
        } catch (Exception e) {
            log.error("Error copying pipeline_info directory: ${e.message}", e)
        }
        
        println "Pipeline complete! 👋"
    }
}
