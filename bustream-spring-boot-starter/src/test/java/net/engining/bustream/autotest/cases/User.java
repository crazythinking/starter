package net.engining.bustream.autotest.cases;

import java.io.Serializable;

/**
 * User Domain
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.2.1
 */
public class User implements Serializable {

    /**
     * spring cloud bus 会默认构造RemoteApplicationEvent，其中已包含id字段，因此payload不能使用再有id作为字段；
     * 容易引起json转换时的冲突；
     */
    //private Long id;
    private Long userId;

    private String name;

    private Integer age;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + userId + ", name='" + name + '\'' + ", age=" + age + '}';
    }

}
