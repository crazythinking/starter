package net.engining.pg.lock.autoconfigure;

import net.engining.pg.lock.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.lock.autoconfigure.autotest.support.User;
import net.engining.pg.lock.autoconfigure.autotest.support.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eric Lu
 * @create 2019-09-21 23:58
 **/
public class SimpleTestCase extends AbstractTestCaseTemplate {

    private static final Random RANDOM = new Random();

    @Autowired
    UserService userService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {
    }

    @Override
    public void testProcess() throws Exception {
        simple1Test();
        simple2Test();
        spel1Test();
        spel2Test();
        programmaticLock();
        reentrantLock();
    }

    @Override
    public void end() throws Exception {

    }

    public void simple1Test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    userService.simple1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 5; i++) {
            executorService.submit(task);
        }
        Thread.sleep(3000);
    }

    public void simple2Test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    userService.simple2("xxx_key");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        Thread.sleep(3000);
    }

    public void spel1Test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    userService.method1(new User(RANDOM.nextLong(), "苞米豆"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        Thread.sleep(3000);
    }

    public void spel2Test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    userService.method2(new User(1L, "苞米豆"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        Thread.sleep(30000);
    }

    public void programmaticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                userService.programmaticLock("admin");
            }
        };
        for (int i = 0; i < 100; i++) {
            executorService.submit(task);
        }
        Thread.sleep(3000);
    }

    public void reentrantLock() {
        userService.reentrantMethod1();
        userService.reentrantMethod1();
        userService.reentrantMethod2();
    }

}
