package net.engining.datasource.autoconfigure.support;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 等同于<tx:annotation-driven mode="aspectj" transaction-manager="transactionManager" />
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-05 10:12
 * @since :
 **/
@Configuration
@EnableTransactionManagement(mode= AdviceMode.ASPECTJ)
public class TransactionManagementContextConfig {
}
