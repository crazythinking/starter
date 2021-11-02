package net.engining.rocksdb.autoconfigure.autotest.cases;

import cn.hutool.core.util.RandomUtil;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.rocksdb.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.rocksdb.autoconfigure.autotest.support.DocBean;
import net.engining.rocksdb.autoconfigure.autotest.support.DocBeanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-27 15:48
 * @since :
 **/
@ActiveProfiles("rocksdb.common")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Autowired
    DocBeanRepository docBeanRepository;

    @Override
    public void initTestData() {
        List<DocBean> list = new ArrayList<>();
        Integer n1 = RandomUtil.randomInt();
        testIncomeDataContext.put("id1", n1);
        list.add(new DocBean(
                n1,
                "XX0193",
                "XX8064",
                "xxxxxx",
                1,
                ErrorCode.CheckError,
                new Date())
        );

        Integer n2 = RandomUtil.randomInt();
        testIncomeDataContext.put("id2", n2);
        list.add(new DocBean(
                n2,
                "XX0210",
                "XX9999",
                "xxxxxxxxxx",
                1,
                ErrorCode.SystemError,
                new Date())
        );

        Integer n3 = RandomUtil.randomInt();
        testIncomeDataContext.put("id3", n3);
        list.add(new DocBean(
                n3,
                "XX0257",
                "XX9999",
                "xxxxxxxxxxxxxxxxxx",
                1,
                ErrorCode.Null,
                new Date())

        );

        docBeanRepository.saveAll(list);
    }

    @Override
    public void assertResult() {

    }

    @Override
    public void testProcess() {
        docBeanRepository.findAll().forEach(docBean -> LOGGER.info(docBean.toString()));

        DocBean docBean = docBeanRepository.findById((Integer) testIncomeDataContext.get("id1")).get();
        LOGGER.debug(docBean.toString());
        docBean.setFirstCode("update test for 1966669899");
        docBeanRepository.save(docBean);
        LOGGER.debug(docBeanRepository.findById((Integer) testIncomeDataContext.get("id1")).get().toString());
        docBeanRepository.deleteById((Integer) testIncomeDataContext.get("id2"));
        LOGGER.debug("{}",docBeanRepository.existsById((Integer) testIncomeDataContext.get("id2")));

    }

    @Override
    public void end() {

    }
}
