package net.engining.redis.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import com.google.common.collect.Sets;
import net.engining.pg.redis.operation.RedissonObject;
import net.engining.pg.redis.utils.RedisUtil;
import net.engining.redis.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author Eric Lu
 * @create 2019-09-21 23:58
 **/
public class SimpleMqTest extends AbstractTestCaseTemplate {

    @Autowired
    RedissonObject redissonObject;

    @Autowired
    RedissonMqService redissonMqService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        redissonMqService.create();
    }

    @Override
    public void end() throws Exception {

    }

    static class User implements Serializable {
        private String name;
        private int age;
        private BigDecimal salary;

        public User(){}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }
    }
}
