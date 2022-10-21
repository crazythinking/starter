package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.api.ContextKey;
import net.engining.control.sdk.FlowTransProcessorTemplate;
import net.engining.transflow.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.transflow.autoconfigure.autotest.support.IntValue2Key;
import net.engining.transflow.autoconfigure.autotest.support.SampleFlowRequest;
import net.engining.transflow.autoconfigure.autotest.support.SimpleFlowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("unchecked")
@ActiveProfiles(profiles={
		"data-sync"
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class FlowDataSyncTestCase extends AbstractTestCaseTemplate {

	@Autowired
	FlowTransProcessorTemplate flowTransProcessorTemplate;

	@Override
	public void initTestData() {
		
		SampleFlowRequest request = new SampleFlowRequest();
		request.setOnlineData("{\"key1\":\"value1\",\"key2\":\"value2\"}");
		request.setChannelRequestSeq("1234567890987654321");
		request.setChannel("test");
		request.setIntValue1(4);
		request.setIntValue2(0);
		testIncomeDataContext.put("request1", request);

		//重复的交易，测试幂等
		SampleFlowRequest request2 = new SampleFlowRequest();
		request.setOnlineData("{\"key1\":\"value1\",\"key2\":\"value2\"}");
		request.setChannelRequestSeq("1234567890987654321");
		request.setChannel("test");
		request.setIntValue1(4);
		request.setIntValue2(0);
		testIncomeDataContext.put("request2", request2);

		SampleFlowRequest request3 = new SampleFlowRequest();
		request.setOnlineData("{\"key1\":\"value1\",\"key2\":\"value2\"}");
		request.setChannelRequestSeq("1234567890987654322");
		request.setChannel("test");
		request.setIntValue1(4);
		request.setIntValue2(0);
		testIncomeDataContext.put("request3", request3);

	}

	@Override
	public void assertResult() {
		Map<Class<? extends ContextKey<?>>, Object> resp =
				(Map<Class<? extends ContextKey<?>>, Object>) testAssertDataContext.get("response1");
		assertThat(resp.get(IntValue2Key.class), equalTo(16));

	}

	@Override
	public void testProcess() throws InterruptedException {
		SampleFlowRequest request = (SampleFlowRequest) testIncomeDataContext.get("request1");
		SimpleFlowResponse resp = flowTransProcessorTemplate.process(request, SimpleFlowResponse.class);
		testAssertDataContext.put("response1", resp);

		SampleFlowRequest request2 = (SampleFlowRequest) testIncomeDataContext.get("request2");
		SimpleFlowResponse resp2 = flowTransProcessorTemplate.process(request2, SimpleFlowResponse.class);
		testAssertDataContext.put("response2", resp2);

		SampleFlowRequest request3 = (SampleFlowRequest) testIncomeDataContext.get("request3");
		for (int i = 0; i < 10; i++) {
			flowTransProcessorTemplate.process(request3, SimpleFlowResponse.class);
		}
		Thread.sleep(20000);
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

}
