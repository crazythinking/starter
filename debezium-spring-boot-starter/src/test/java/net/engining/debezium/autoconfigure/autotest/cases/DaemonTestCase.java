package net.engining.debezium.autoconfigure.autotest.cases;

import java.util.concurrent.TimeUnit;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-09 12:19
 * @since :
 **/
public class DaemonTestCase {

    public static void main(String[] args) {
        Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
        //Daemon属性需要在启动线程之前设置，不能在线程启动之后设置
        thread.setDaemon(false);
        thread.start();

        Thread thread1 = new Thread(new DaemonRunner1(), "DaemonRunner2");
        thread1.start();
    }

    static class DaemonRunner implements Runnable {
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(10); //睡眠10秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("DaemonThread finally run. ");
            }
        }
    }

    static class DaemonRunner1 implements Runnable {
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(5); //睡眠10秒
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("DaemonThread finally run. ");
            }
        }
    }
}
