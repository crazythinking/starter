package net.engining.transflow.autoconfigure.autotest.cases;

import com.google.common.collect.Maps;
import net.engining.control.api.ContextKey;
import net.engining.control.api.FlowDispatcher;
import net.engining.control.api.key.ChannelKey;
import net.engining.control.api.key.ChannelRequestSeqKey;
import net.engining.control.api.key.OnlineDataKey;
import net.engining.transflow.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.transflow.autoconfigure.autotest.support.IntValue1Key;
import net.engining.transflow.autoconfigure.autotest.support.IntValue2Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class NormalFlowTestCase extends AbstractTestCaseTemplate {

	@Autowired
	private FlowDispatcher dispatcher;

	@Override
	public void initTestData() {
		
		Map<Class<? extends ContextKey<?>>, Object> req = Maps.newHashMap();
		req.put(OnlineDataKey.class, "{\"key1\":\"value1\",\"key2\":\"value2\"}");
		req.put(ChannelRequestSeqKey.class, "1234567890987654321");
		req.put(ChannelKey.class, "test");
		req.put(IntValue1Key.class, 4);
		req.put(IntValue2Key.class, 0);
		
		testIncomeDataContext.put("request1", req);

		//重复的交易，测试幂等
		req = Maps.newHashMap();
		req.put(OnlineDataKey.class, "{\"key1\":\"value1\",\"key2\":\"value2\"}");
		req.put(ChannelRequestSeqKey.class, "1234567890987654321");
		req.put(ChannelKey.class, "test");
		req.put(IntValue1Key.class, 4);
		req.put(IntValue2Key.class, 0);

		testIncomeDataContext.put("request2", req);

	}

	@Override
	public void assertResult() {
		Map<Class<? extends ContextKey<?>>, Object> resp =
				(Map<Class<? extends ContextKey<?>>, Object>) testAssertDataContext.get("response1");
		assertThat(resp.get(IntValue2Key.class), equalTo(16));

	}

	@Override
	public void testProcess() {
		Map<Class<? extends ContextKey<?>>, Object> resp = dispatcher.process(
				"sample",
				(Map<Class<? extends ContextKey<?>>, Object>) testIncomeDataContext.get("request1")
		);
		testAssertDataContext.put("response1", resp);

		Map<Class<? extends ContextKey<?>>, Object> resp2 = dispatcher.process(
				"sample",
				(Map<Class<? extends ContextKey<?>>, Object>) testIncomeDataContext.get("request2")
		);
		testAssertDataContext.put("response2", resp2);
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

}
