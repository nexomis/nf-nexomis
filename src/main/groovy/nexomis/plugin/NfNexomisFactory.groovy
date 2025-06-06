package nexomis.plugin

import groovy.transform.CompileStatic
import nextflow.Session
import nextflow.trace.TraceObserver
import nextflow.trace.TraceObserverFactory

/**
 * Implements a factory object required to create
 * the {@link NfNexomisObserver} instance.
 */
@CompileStatic
class NfNexomisFactory implements TraceObserverFactory {

    @Override
    Collection<TraceObserver> create(Session session) {
        return List.<TraceObserver>of(new NfNexomisObserver())
    }

}
