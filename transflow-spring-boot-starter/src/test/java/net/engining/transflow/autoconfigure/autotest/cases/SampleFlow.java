package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.AbstractFlow;
import net.engining.control.core.flow.FlowDefinition;
import net.engining.control.core.invoker.DetermineFinalResult;
import net.engining.control.core.invoker.TransactionSeperator;
import net.engining.control.core.invoker.WriteInboundJournal;
import net.engining.control.core.invoker.WriteJournalUpdateResult;
import net.engining.transflow.autoconfigure.autotest.support.IntValue2Key;

@FlowDefinition(
        code = "sample",
        name = "示列流程",
        desc = "示列流程. 用于测试",
        invokers = {
                WriteInboundJournal.class,
                TransactionSeperator.class,
                SampleInvoker1.class,
                SampleInvoker2.class,
                //SampleInvoker3.class,
                TransactionSeperator.class,
                SampleInvoker4.class,
                TransactionSeperator.class,
                DetermineFinalResult.class,
                WriteJournalUpdateResult.class
        },
        response = {
                IntValue2Key.class
        }
)
public class SampleFlow extends AbstractFlow {
}
