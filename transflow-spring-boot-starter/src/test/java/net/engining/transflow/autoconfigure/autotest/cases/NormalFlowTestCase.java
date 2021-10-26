package net.engining.transflow.autoconfigure.autotest.cases;

import net.engining.control.sdk.FlowTransProcessorTemplate;
import net.engining.transflow.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.transflow.autoconfigure.autotest.support.SampleFlowRequest;
import net.engining.transflow.autoconfigure.autotest.support.SimpleFlowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class NormalFlowTestCase extends AbstractTestCaseTemplate {

	@Autowired
	FlowTransProcessorTemplate flowTransProcessorTemplate;

	@Override
	public void initTestData() {
		
	}

	@Override
	public void assertResult() {
		SimpleFlowResponse resp = (SimpleFlowResponse) testAssertDataContext.get("response1");
		assertThat(resp.getIntValue2(), equalTo(16));

	}

	@Override
	public void testProcess() {
		SampleFlowRequest request = new SampleFlowRequest();
		request.setOnlineData("{\"key1\":\"value1\",\"key2\":\"value2\"}");
		request.setChannelRequestSeq("1234567890987654321");
		request.setChannel("test");
		request.setIntValue1(4);
		request.setIntValue2(0);

		SimpleFlowResponse resp = flowTransProcessorTemplate.process(request, SimpleFlowResponse.class);
		testAssertDataContext.put("response1", resp);

		//重复的交易，测试幂等
		SimpleFlowResponse resp2 = flowTransProcessorTemplate.process(request, SimpleFlowResponse.class);
		testAssertDataContext.put("response2", resp2);
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

}
