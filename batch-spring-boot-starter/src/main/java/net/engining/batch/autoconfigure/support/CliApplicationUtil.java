package net.engining.batch.autoconfigure.support;

import cn.hutool.core.lang.Console;
import net.engining.pg.batch.sdk.infrastructure.utils.BatchCommonLogicUtils;
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

    /**
     * Runs the specified main class with the provided arguments, logging necessary information.
     *
     * @param  args                    the arguments to be passed to the main class
     * @param  mainClass               the main class to be run
     * @param  defaultUnknownCliReturn the default value in case of an unknown CLI return
     */
    public static void run(String[] args, Class mainClass, String defaultUnknownCliReturn) {
        Console.log("{}{}", BatchCommonLogicUtils.BATCH_CLI_PID, SystemUtils.getCurrentPid());
        ConfigurableApplicationContext configurableApplicationContext = null;
        try {
            configurableApplicationContext = SpringApplication.run(mainClass, args);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
        } finally {
            exitApplication(configurableApplicationContext, defaultUnknownCliReturn);
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
            Console.error("{}{}", BatchCommonLogicUtils.BATCH_CLI_RETURN, batchCliReturn);
            exitCode = -1;
        }
        System.exit(exitCode);
    }

}
