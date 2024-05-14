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
 * CLI 启动抽象类
 *
 */
public class AbstractCliApplication {

    public static void main(String[] args) {
        Console.log("{}{}", BatchCommonLogicUtils.BATCH_CLI_PID, SystemUtils.getCurrentPid());
        ConfigurableApplicationContext configurableApplicationContext = null;
        try {
            configurableApplicationContext = SpringApplication.run(AbstractCliApplication.class, args);
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
            Console.error("{}{}", BatchCommonLogicUtils.BATCH_CLI_RETURN, JSON.toJSONString(ErrorCode.UnknowFail));
            exitCode = -1;
        }
        System.exit(exitCode);
    }
}