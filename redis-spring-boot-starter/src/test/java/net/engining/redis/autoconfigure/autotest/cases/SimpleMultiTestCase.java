package net.engining.redis.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.engining.pg.redis.operation.RedissonObjectOperation;
import net.engining.pg.redis.utils.RedisUtil;
import net.engining.redis.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eric Lu
 * @create 2019-09-21 23:58
 **/
@ActiveProfiles({
        "redisson.mutil"
})
public class SimpleMultiTestCase extends AbstractTestCaseTemplate {

    private User user;

    @Override
    public void initTestData() throws Exception {
        user = new User();
        user.setAge(30);
        user.setName("张三");
        user.setSalary(BigDecimal.valueOf(10000.00));
        user.msg = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            user.msg.add("this is "+i);
        }

        Set<String> set1 = Sets.newHashSet("aaa","bbb","ccc","ddd","eee");
        Set<String> set2 = Sets.newHashSet("aaa","fff","ccc","ddd","hhh","mmm");
        Sets.SetView<String> setView = Sets.symmetricDifference(set1, set2);
        Console.log(setView.toString());

    }

    @Override
    public void assertResult() throws Exception {
        User user1 = RedisUtil.getRedissonObjectOperation("one").getValue("user1");
        Assert.isTrue(user.getName().equals(user1.name), "user对象的值不相等");

        //BitMap
        boolean b = RedisUtil.getBitmapHandler().get("loginId", 3L);
        Assert.isTrue(b, "状态不相等");
        boolean b2 = RedisUtil.getBitmapHandler("one").get("loginId", 4L);
        Assert.isTrue(!b2, "状态不相等");
        boolean b3 = RedisUtil.getBitmapHandler("one").get("loginId", 0L);
        Assert.isTrue(!b3, "状态不相等");
    }

    @Override
    public void testProcess() throws Exception {
        //指定到数据源“one”
        RedisUtil.getRedissonObjectOperation("one").setValue("user1", user);

        //BitMap
        //案例，根据loginId标识是否登录，offset用于关联在数据库内保存的loginId
        //默认数据源
        RedisUtil.getBitmapHandler().set("loginId", 3L, true);
        //指定到数据源“one”
        RedisUtil.getBitmapHandler("one").set("loginId", 4L, false);
        RedisUtil.getBitmapHandler("one").set("loginId", 0L, false);
        RedisUtil.getHyperLogLogHandler("one").add("one-hyperlog1", Duration.ofMinutes(100), "a","b", "c", "d", "d", "c");

        Map<String, String> m = Maps.newHashMap();
        m.put("1", "a");
        m.put("2", "b");
        //指定到数据源“one”
        RedisUtil.getStringHandler("one").mset(m);

    }

    @Override
    public void end() throws Exception {
        //redisTemplate.delete("user1");
    }

    static class User implements Serializable {
        private String name;
        private int age;
        private BigDecimal salary;
        private List<String> msg;

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

        public List<String> getMsg() {
            return msg;
        }

        public void setMsg(List<String> msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", salary=" + salary +
                    ", msg=" + msg +
                    '}';
        }
    }
}
