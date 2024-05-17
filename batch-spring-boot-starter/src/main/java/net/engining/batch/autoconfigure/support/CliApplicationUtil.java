package net.engining.batch.autoconfigure.support;

import cn.hutool.core.lang.Console;
import com.alibaba.fastjson.JSON;
import net.engining.pg.batch.sdk.infrastructure.utils.BatchCommonLogicUtils;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.SystemUtils;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Eric Lu
 * @date 2024-05-14 17:49
 **/
public class CliApplicationUtil {

    public static void run(String[] args, Class mainClass, String defaultBatchCliReturn) {
        Console.log("{}{}", BatchCommonLogicUtils.BATCH_CLI_PID, SystemUtils.getCurrentPid());
        ConfigurableApplicationContext configurableApplicationContext = null;
        try {
            configurableApplicationContext = SpringApplication.run(mainClass, args);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
        } finally {
            exitApplication(configurableApplicationContext, defaultBatchCliReturn);
        }
    }

    /**
     * 通过System exit 关闭
     *
     * @param configurableApplicationContext 上下文
     */
    private static void exitApplication(ConfigurableApplicationContext configurableApplicationContext, String batchCliReturn) {
        int exitCode = 0;
        if (ValidateUtilExt.isNotNullOrEmpty(configurableApplicationContext)) {
            SpringApplication.exit(configurableApplicationContext, () -> {
                // no errors
                return 0;
            });
        } else {
            //List<AbstractBatchJobRunner.ExecutionStatus> list = Lists.newArrayList();
            //list.add(new AbstractBatchJobRunner.ExecutionStatus(-1L, new ExitStatus("UNKNOWN", "未知错误")));
            Console.error("{}{}", BatchCommonLogicUtils.BATCH_CLI_RETURN, JSON.toJSONString(ErrorCode.UnknowFail));
            Console.error("{}{}", BatchCommonLogicUtils.BATCH_CLI_RETURN, batchCliReturn);
            exitCode = -1;
        }
        System.exit(exitCode);
    }

}
