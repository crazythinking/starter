package net.engining.drools.autoconfigure.core;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author Eric Lu
 */
public class KieTemplate {

    private KieEngine kieEngine;

    public KieTemplate(KieEngine kieEngine){
        this.kieEngine = kieEngine;
    }

    public void execute() {

    }
}
