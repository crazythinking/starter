package net.engining.minio.autoconfigure.autotest.cases;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.engining.minio.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.storage.minio.operators.ObjectOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-03-16 17:27
 * @since :
 **/
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class SimpleTestCase extends AbstractTestCaseTemplate {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ObjectOperations objectOperations;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        Multimap<String, String> metaData = ArrayListMultimap.create();
        metaData.put("doc", "时间轮");
        metaData.put("doc", "Timing Wheels");
        metaData.put("type", "ppt");
        objectOperations.uploadObject(
                "D:\\360Downloads\\TimingWheels.ppt",
                "TimingWheels.ppt",
                metaData
        );
    }

    @Override
    public void end() throws Exception {

    }
}
