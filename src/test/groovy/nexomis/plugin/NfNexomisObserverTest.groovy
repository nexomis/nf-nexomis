package nexomis.plugin

import nextflow.Session
import spock.lang.Specification

/**
 * Implements a basic factory test
 *
 */
class NfNexomisObserverTest extends Specification {

    def 'should create the observer instance' () {
        given:
        def factory = new NfNexomisFactory()
        when:
        def result = factory.create(Mock(Session))
        then:
        result.size() == 1
        result.first() instanceof NfNexomisObserver
    }

}
