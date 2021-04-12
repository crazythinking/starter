package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.FlowContext;
import net.engining.control.core.invoker.AbstractSkippableInvoker;
import net.engining.control.core.invoker.InvokerDefinition;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;

/**
 * @author binarier
 *
 */
@InvokerDefinition(
	name = "square invoker3"
)
public class SampleInvoker3 extends AbstractSkippableInvoker
{
	@Override
	public void invoke(FlowContext ctx)
	{
		throw new ErrorMessageException(ErrorCode.SystemError, "SampleInvoker3 test for ErrorMessages");
	}

}
