package net.engining.bustream.autotest.cases;

import java.io.Serializable;

/**
 * User Domain
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.2.1
 */
public class User2 implements Serializable {

    /**
     * spring cloud bus 会默认构造RemoteApplicationEvent，其中已包含id字段，因此payload不能使用再有id作为字段；
     * 容易引起json转换时的冲突；
     */
    //private Long id;
    private Long userId2;

    private String name2;

    private Integer age;

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long userId2) {
        this.userId2 = userId2;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User2{" +
                "userId2=" + userId2 +
                ", name2='" + name2 + '\'' +
                ", age=" + age +
                '}';
    }
}
