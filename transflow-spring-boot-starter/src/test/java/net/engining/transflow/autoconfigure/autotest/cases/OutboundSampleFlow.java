package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.core.flow.AbstractFlow;
import net.engining.control.core.flow.FlowDefinition;
import net.engining.control.core.invoker.DetermineFinalResult;
import net.engining.control.core.invoker.TransactionSeperator;
import net.engining.control.core.invoker.WriteInboundJournal;
import net.engining.control.core.invoker.WriteJournalUpdateResult;
import net.engining.transflow.autoconfigure.autotest.support.IntValue2Key;
import org.springframework.stereotype.Service;

/**
 * outbound示例
 *
 * @author ChenBao
 * @version 1.0
 * @date 2021/4/1 13:34
 * @since 1.0
 */
@SuppressWarnings("deprecation")
@FlowDefinition(
        code = "outboundsample",
        name = "outbound示列流程",
        desc = "outbound示列流程. 用于测试",
        invokers = {
                WriteInboundJournal.class,
                TransactionSeperator.class,
                //outbound invoker test class
                OutboundSampleInvoker.class,
                TransactionSeperator.class,
                DetermineFinalResult.class,
                WriteJournalUpdateResult.class
        },
        response = {
                IntValue2Key.class
        }
)
@Service
public class OutboundSampleFlow extends AbstractFlow {
}
