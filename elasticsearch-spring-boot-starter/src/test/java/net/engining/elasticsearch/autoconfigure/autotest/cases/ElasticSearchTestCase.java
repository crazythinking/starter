package net.engining.elasticsearch.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import net.engining.elasticsearch.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.elasticsearch.autoconfigure.autotest.support.DocBean;
import net.engining.elasticsearch.autoconfigure.autotest.support.DocBeanRepositoriesServiceImpl;
import net.engining.elasticsearch.autoconfigure.autotest.support.ExceptionDoc;
import net.engining.elasticsearch.autoconfigure.autotest.support.ExceptionDocRepoService;
import net.engining.elasticsearch.autoconfigure.autotest.support.PkBean;
import net.engining.pg.support.core.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-03 17:15
 * @since :
 **/
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ElasticSearchTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchTestCase.class);

    @Autowired
    DocBeanRepositoriesServiceImpl docBeanRepositoriesService;

    @Autowired
    ExceptionDocRepoService exceptionDocRepoService;

    public Iterator<DocBean> all() {
        return docBeanRepositoriesService.findAll();
    }

    @Override
    public void initTestData() {

        docBeanRepositoriesService.createIndex();
        List<DocBean> list = new ArrayList<>();
        list.add(new DocBean(
                new PkBean(RandomUtil.randomLong(), RandomUtil.randomString(1)),
                "XX0193",
                "XX8064",
                "xxxxxx",
                1,
                ErrorCode.CheckError,
                new Date(),
                new DocBean.Agent("bf", "bf"))
        );
        list.add(new DocBean(
                new PkBean(RandomUtil.randomLong(), RandomUtil.randomString(1)),
                "XX0210",
                "XX9999",
                "xxxxxxxxxx",
                1,
                ErrorCode.SystemError,
                new Date(),
                new DocBean.Agent("bf", "bf"))
        );
        list.add(new DocBean(
                new PkBean(RandomUtil.randomLong(), RandomUtil.randomString(1)),
                "XX0257",
                "XX9999",
                "xxxxxxxxxxxxxxxxxx",
                1,
                ErrorCode.Null,
                new Date(),
                new DocBean.Agent("bf", "bf"))

        );
        //list.add(new DocBean(RandomUtil.randomLong(), "XX0193", "XX8064", "xxxxxx", 1));
        //list.add(new DocBean(RandomUtil.randomLong(), "XX0210", "XX9999", "xxxxxxxxxx", 1));
        //list.add(new DocBean(RandomUtil.randomLong(), "XX0257", "XX9999", "xxxxxxxxxxxxxxxxxx", 1));
        //list.add(new DocBean(RandomUtil.randomLong(), "XX0193", "XX8064", "rexx", 1));
        //list.add(new DocBean(RandomUtil.randomLong(), "XX0210", "XX9999", "xbox's", 1));
        //list.add(new DocBean(RandomUtil.randomLong(), "XX0257", "XX9999", "xxxxxxxxxxxxxxxxxx", 1));
        docBeanRepositoriesService.saveAll(list);
    }

    @Override
    public void assertResult() {
        List<DocBean> beans = docBeanRepositoriesService.findByFirstCodeAndSecondCode("XX0210", "XX9999");
        beans.forEach(docBean -> {
            assertThat(docBean.getFirstCode(), is("XX0210"));
            assertThat(docBean.getSecondCode(), is("XX9999"));
        });
        Console.log("get beans count : {}", beans.size());

        List<DocBean> beans2 = docBeanRepositoriesService.findByFirstCodeAndSecondCodeWithNative("XX0210", "XX9999");
        beans2.forEach(docBean -> {
            LOGGER.info("docBean : {}", docBean);
        });

        docBeanRepositoriesService.findByFirstCode("XX0257");
        Page<DocBean> docBeanPage = docBeanRepositoriesService.findByContent("xxxxxx");
        docBeanPage.get().forEach(docBean -> Console.log(docBean.getCreateTime()));

        all();

        List<ExceptionDoc> beans3 = exceptionDocRepoService.findWithNative("ConnectException");
        beans3.forEach(exceptionDoc -> {
            LOGGER.info("exceptionDoc : {}", exceptionDoc);
        });
    }

    @Override
    public void testProcess() {

    }

    @Override
    public void end() {

    }
}
