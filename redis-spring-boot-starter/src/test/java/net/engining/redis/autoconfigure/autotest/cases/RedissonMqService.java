package net.engining.redis.autoconfigure.autotest.cases;

import net.engining.pg.redis.annotation.RedissonMqConsumer;
import net.engining.pg.redis.annotation.RedissonMqPublish;
import net.engining.pg.redis.enums.MqModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-06-28 14:40
 * @since :
 **/
@Service
public class RedissonMqService {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonMqService.class);

    @RedissonMqPublish(name = "userTopic")
    public SimpleTest.User create(){
        SimpleTest.User user = new SimpleTest.User();
        user.setAge(30);
        user.setName("张三");
        user.setSalary(BigDecimal.valueOf(10000.00));
        return user;
    }

    @RedissonMqConsumer(name = "userTopic", model = MqModel.PRECISE)
    public void changeUser(SimpleTest.User user){
        user.setSalary(BigDecimal.valueOf(50000L));
        LOGGER.info(user.toString());
    }
}
