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
public class SimpleTestCase extends AbstractTestCaseTemplate {

    @Autowired
    RedissonObjectOperation redissonObject;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    RedissonCacheService redissonCacheService;

    @Autowired
    RedisCacheService redisCacheService;

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
        User user1 = redissonObject.getValue("user1");
        Assert.isTrue(user.getName().equals(user1.name), "user对象的值不相等");

        //BitMap
        boolean b = RedisUtil.getBitmapHandler().get("loginId", 3L);
        Assert.isTrue(b, "状态不相等");
        boolean b2 = RedisUtil.getBitmapHandler().get("loginId", 4L);
        Assert.isTrue(!b2, "状态不相等");
        //boolean b3 = RedisUtil.getBitmapHandler(5).get("loginId", 0L);
        //Assert.isTrue(!b3, "状态不相等");
    }

    @Override
    public void testProcess() throws Exception {
        redissonObject.setValue("user1", user);

        //BitMap
        //案例，根据loginId标识是否登录，offset用于关联在数据库内保存的loginId
        //默认DbIndex
        RedisUtil.getBitmapHandler().set("loginId", 3L, true);
        //这里会触发创建新的一套连接池到指定的DbIndex
        //RedisUtil.getBitmapHandler(5).set("loginId", 4L, false);
        //RedisUtil.getBitmapHandler(5).set("loginId", 4L, false);

        RedisUtil.getBitmapHandler().set("loginId2", 1L, false, Duration.ofMinutes(10));

        RedisUtil.getGeoHandler().add("geo1", new Point(1.0, 2.0), "ggggeo", Duration.ofMinutes(10));

        RedisUtil.getHyperLogLogHandler().add("hyperlog1", Duration.ofMinutes(100), "a","b", "c", "d", "d", "c");

        Map<String, String> m = Maps.newHashMap();
        m.put("1", "a");
        m.put("2", "b");
        RedisUtil.getStringHandler().mset(m);

        RedisUtil.getNumberHandler();

        String a = redissonCacheService.cachable("-test-cache");
        Console.log(a);
        String b = redissonCacheService.cachable2("-test-cache");
        Console.log(b);
        String c = redissonCacheService.cachable3("-test-cache");
        Console.log(c);
        redisCacheService.originCachable("origin-test-cache");

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
