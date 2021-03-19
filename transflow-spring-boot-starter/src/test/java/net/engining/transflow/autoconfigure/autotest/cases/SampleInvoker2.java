package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.FlowContext;
import net.engining.control.core.invoker.AbstractSkippableInvoker;
import net.engining.control.core.invoker.InvokerDefinition;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.transflow.autoconfigure.autotest.support.IntValue2Key;

import java.util.Map;

/**
 * 把 {@link IntValue2Key} 作平方后写入 {@link IntValue2Key}
 * @author binarier
 *
 */
@InvokerDefinition(
	name = "square invoker2",
	requires = IntValue2Key.class,
	results = IntValue2Key.class
)
public class SampleInvoker2 extends AbstractSkippableInvoker
{
	@Override
	public void invoke(FlowContext ctx)
	{
		int value = ctx.get(IntValue2Key.class);
		value = value * value;
		
		ctx.put(IntValue2Key.class, value);

		throw new ErrorMessageException(ErrorCode.SystemError, "SampleInvoker2 test for ErrorMessages");
	}

	@Override
	protected boolean skip2Customrized(Map<String, String> parameters) {
		return ValidateUtilExt.isNotNullOrEmpty(parameters.get("skip"));
	}


}
