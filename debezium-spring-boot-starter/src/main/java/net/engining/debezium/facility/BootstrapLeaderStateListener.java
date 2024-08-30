package net.engining.debezium.facility;

import com.alipay.sofa.jraft.rhea.LeaderStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 使 DebeziumServerBootstrap 的启动依赖于Sofa-JRaft LeaderStateListener，当前节点为Leader时启动，否则停止；
 * @author Eric Lu
 */
public class BootstrapLeaderStateListener implements LeaderStateListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapLeaderStateListener.class);

    private final DebeziumServerBootstrap debeziumServerBootstrap;

    public BootstrapLeaderStateListener(DebeziumServerBootstrap debeziumServerBootstrap) {
        this.debeziumServerBootstrap = debeziumServerBootstrap;
    }

    @Override
    public void onLeaderStart(long newTerm) {
        LOGGER.info(
                "this Node is leader now, newTerm: {}; will start debezium {}",
                newTerm,
                debeziumServerBootstrap.getSuffix()
        );
        //启动DebeziumServerBootstrap
        if (debeziumServerBootstrap.isRunning()) {
            LOGGER.warn("debezium {} is already running, will not start again", debeziumServerBootstrap.getSuffix());
            return;
        }
        //不要马上启动，底层的资源需要有初始化的过程，包括：前序LeadNode的资源回收，VHeaKv数据一致性同步等，暂停当前线程60s
        try {
            TimeUnit.MILLISECONDS.sleep(60 * 1000);
        }
        catch (InterruptedException e) {
            LOGGER.error("start debezium {} thread sleep error", debeziumServerBootstrap.getSuffix(), e);
        }
        debeziumServerBootstrap.start();
    }

    @Override
    public void onLeaderStop(long oldTerm) {
        LOGGER.info(
                "this Node is not leader now, oldTerm: {}; will stop debezium {}",
                oldTerm,
                debeziumServerBootstrap.getSuffix()
        );
        //停止DebeziumServerBootstrap
        if (!debeziumServerBootstrap.isRunning()) {
            LOGGER.warn("debezium {} is already stopped, will not stop again", debeziumServerBootstrap.getSuffix());
            return;
        }
        debeziumServerBootstrap.stop();
    }
}
