package nexomis.plugin

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.trace.TraceObserver

/**
 * Implements an observer that allows implementing custom
 * logic on nextflow execution events.
 */
@Slf4j
@CompileStatic
class NfNexomisObserver implements TraceObserver {

    @Override
    void onFlowCreate(Session session) {
        
        println """
            |            #################################################
            |            #    _  _                             _         #
            |            #   | \\| |  ___  __ __  ___   _ __   (_)  __    #
            |            #   | .` | / -_) \\ \\ / / _ \\ | '  \\  | | (_-<   #
            |            #   |_|\\_| \\___| /_\\_\\ \\___/ |_|_|_| |_| /__/   #
            |            #                                               #
            |            #################################################
            |            https://github.com/nexomis     https://nexomis.io
            |"""
        
    }

    @Override
    void onFlowComplete() {
        println "Pipeline complete! 👋"
    }
}
