package net.engining.redis.autoconfigure.autotest.cases;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-06-28 15:35
 * @since :
 **/
public class FastThreadTest {

    public static void main(String[] args) throws InterruptedException {

        Things t1 = new Things(RandomUtil.randomInt(0,100));

        Things t2 = new Things(RandomUtil.randomInt(0,100));

        ThingsRun tr1 = new ThingsRun(t1);
        ThingsRun tr2 = new ThingsRun(t2);

        FastThreadLocalThread ft1 = new FastThreadLocalThread(tr1);
        ft1.start();
        FastThreadLocalThread ft2 = new FastThreadLocalThread(tr2);
        ft2.start();
        Console.log(ft1.willCleanupFastThreadLocals());
        Console.log(ft2.willCleanupFastThreadLocals());

        while (ft1.getState().equals(Thread.State.RUNNABLE) || ft2.getState().equals(Thread.State.RUNNABLE)) {
            Thread.sleep(1000);
        }

    }

    public static class Things {
        //唯一标识
        private final int ID;
        private final FastThreadLocal<String> threadLocal;

        public Things(int id) {
            this.ID = id;
            //在哪个线程内实例化，即属于哪个线程
            this.threadLocal = new FastThreadLocal<String>() {
                @Override
                protected String initialValue() {
                    return "FastThreadLocal:Things:"+ID;
                }
            };
        }

        public void say() {
            Console.log(Thread.currentThread() + " " + threadLocal.get());

        }

        public void set(String s) {
            threadLocal.set(s + "for:" + ID);
        }

        public void set(Integer s, FastThreadLocal<Integer> threadLocal){
            threadLocal.set(s);
            Console.log(Thread.currentThread() + "@" + threadLocal.get());
        }
    }

    public static class ThingsRun implements Runnable{
        private final Things things;
        public ThingsRun(Things things){
            this.things=things;
        }
        @Override
        public void run() {
            try {
                things.say();
                for (int i = 0; i < 10; i++) {
                    things.set(System.currentTimeMillis()+"@"+i);
                    FastThreadLocal<Integer> threadLocal = new FastThreadLocal<>();
                    things.set(i,threadLocal);
                }
                things.say();
            }
            finally {
                //用完ThreadLocal清理，防止内存泄露; 清理当前线程绑定的所有FastThreadLocal
                FastThreadLocal.removeAll();
            }
        }
    }
}
