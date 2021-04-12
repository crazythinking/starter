package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.FlowContext;
import net.engining.control.core.invoker.AbstractSkippableInvoker;
import net.engining.control.core.invoker.InvokerDefinition;
import net.engining.transflow.autoconfigure.autotest.support.IntValue1Key;
import net.engining.transflow.autoconfigure.autotest.support.StringValue1Key;
import net.engining.transflow.autoconfigure.autotest.support.StringValue2Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@InvokerDefinition(
        name = "fork-join invoker4",
        requires = IntValue1Key.class,
        asynSubInvokers = {
                SampleInvoker5.class,
                SampleInvoker6.class
        }
)
public class SampleInvoker4 extends AbstractSkippableInvoker {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleInvoker4.class);

    @Override
    public void invoke(FlowContext ctx) {
        LOGGER.debug("get value from asyn sub invoker SampleInvoker5=[{}]", ctx.get(StringValue1Key.class));
        LOGGER.debug("get value from asyn sub invoker SampleInvoker6=[{}]", ctx.get(StringValue2Key.class));
    }

}
