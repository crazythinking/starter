package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.FlowContext;
import net.engining.control.core.invoker.AbstractSkippableInvoker;
import net.engining.control.core.invoker.InvokerDefinition;
import net.engining.transflow.autoconfigure.autotest.support.IntValue1Key;
import net.engining.transflow.autoconfigure.autotest.support.StringValue1Key;

@InvokerDefinition(
	name = "square invoker5",
	requires = IntValue1Key.class,
	results = StringValue1Key.class
)
public class SampleInvoker5 extends AbstractSkippableInvoker
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
		ctx.put(StringValue1Key.class, String.valueOf(value));
		
	}

}
