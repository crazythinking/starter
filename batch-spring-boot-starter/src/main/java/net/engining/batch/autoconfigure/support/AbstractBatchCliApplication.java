package net.engining.batch.autoconfigure.support;

import cn.hutool.core.lang.Console;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import net.engining.pg.batch.runner.AbstractBatchJobRunner;
import net.engining.pg.batch.sdk.infrastructure.utils.BatchCommonLogicUtils;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.SystemUtils;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.batch.core.ExitStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * Spring Batch CLI 启动抽象类
 *
 */
public class AbstractBatchCliApplication {

    public static void main(String[] args) {
        Console.log("{}{}", BatchCommonLogicUtils.BATCH_CLI_PID, SystemUtils.getCurrentPid());
        ConfigurableApplicationContext configurableApplicationContext = null;
        try {
            configurableApplicationContext = SpringApplication.run(AbstractBatchCliApplication.class, args);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
        } finally {
            exitApplication(configurableApplicationContext);
        }
    }

    /**
     * 通过System exit 关闭
     *
     * @param configurableApplicationContext 上下文
     */
    private static void exitApplication(ConfigurableApplicationContext configurableApplicationContext) {
        int exitCode = 0;
        if (ValidateUtilExt.isNotNullOrEmpty(configurableApplicationContext)) {
            SpringApplication.exit(configurableApplicationContext, () -> {
                // no errors
                return 0;
            });
        } else {
            List<AbstractBatchJobRunner.ExecutionStatus> list = Lists.newArrayList();
            list.add(new AbstractBatchJobRunner.ExecutionStatus(-1L, new ExitStatus("UNKNOWN", "未知错误")));
            Console.error("{}{}", BatchCommonLogicUtils.BATCH_CLI_RETURN, JSON.toJSONString(list));
            exitCode = -1;
        }
        System.exit(exitCode);
    }
}
