package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.FlowContext;
import net.engining.control.core.invoker.AbstractSkippableInvoker;
import net.engining.control.core.invoker.InvokerDefinition;
import net.engining.transflow.autoconfigure.autotest.support.IntValue1Key;
import net.engining.transflow.autoconfigure.autotest.support.StringValue2Key;

@InvokerDefinition(
	name = "square invoker6",
	requires = IntValue1Key.class,
	results = StringValue2Key.class
)
public class SampleInvoker6 extends AbstractSkippableInvoker
{
	@Override
	public void invoke(FlowContext ctx)
	{
		int value = ctx.get(IntValue1Key.class);
		value = value * value;

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ctx.put(StringValue2Key.class, String.valueOf(value));
		
	}

}
