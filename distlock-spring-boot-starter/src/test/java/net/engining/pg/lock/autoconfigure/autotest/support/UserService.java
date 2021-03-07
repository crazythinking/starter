package net.engining.pg.lock.autoconfigure.autotest.support;

import net.engining.pg.lock.LockInfo;
import net.engining.pg.lock.LockTemplate;
import net.engining.pg.lock.annotation.DistLock;
import net.engining.pg.lock.excutor.ZookeeperLockExecutor;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    @Autowired
    LockTemplate lockTemplate;

    private int counter = 1;

    @DistLock(executor = ZookeeperLockExecutor.class)
    public void simple1() {
        System.out.println("执行简单方法1 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));
    }

    @DistLock(keys = "#myKey")
    public void simple2(String myKey) {
        System.out.println("执行简单方法2 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));

    }

    @DistLock(keys = "#user.id")
    public User method1(User user) {
        System.out.println("执行spel方法1 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));
        return user;
    }

    @DistLock(keys = {"#user.id", "#user.name"}, acquireTimeout = 5000, expire = 5000)
    public User method2(User user) {
        System.out.println("执行spel方法2 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));
        //模拟锁占用
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void programmaticLock(String userId) {

        // 各种查询操作 不上锁
        // ...
        // 获取锁
        final LockInfo lockInfo = lockTemplate.lock(userId, 30000L, 5000L, ZookeeperLockExecutor.class);
        if (null == lockInfo) {
            throw new RuntimeException("业务处理中,请稍后再试");
        }
        // 获取锁成功，处理业务
        try {
            System.out.println("执行简单方法1 , 当前线程:" + Thread.currentThread().getName() + " , counter：" + (counter++));
        } finally {
            //释放锁
            lockTemplate.releaseLock(lockInfo);
        }
        //结束
    }


    @DistLock(keys = "1", expire = 60000)
    public void reentrantMethod1() {
        System.out.println("reentrantMethod1 " + getClass());
        counter++;
    }

    @DistLock(keys = "1")
    public void reentrantMethod2() {
        System.out.println("reentrantMethod2 " + getClass());
        counter++;
    }

}