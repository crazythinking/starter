package net.engining.debezium.facility;

import com.alipay.sofa.jraft.rhea.LeaderStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 使 DebeziumServerBootstrap 的启动依赖于Sofa-JRaft LeaderStateListener，当前节点为Leader时启动，否则停止；
 */
public class BootstrapLeaderStateListener implements LeaderStateListener {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapLeaderStateListener.class);

    private final Map<String, DebeziumServerBootstrap> debeziumServerBootstrapMap;

    public BootstrapLeaderStateListener(Map<String, DebeziumServerBootstrap> debeziumServerBootstrapMap) {
        this.debeziumServerBootstrapMap = debeziumServerBootstrapMap;
    }

    @Override
    public void onLeaderStart(long newTerm) {
        LOGGER.info("this Node is leader now, newTerm: {}; will start debezium", newTerm);
        //启动DebeziumServerBootstrap
        debeziumServerBootstrapMap.forEach((k, v) -> {
            v.start();
        });
    }

    @Override
    public void onLeaderStop(long oldTerm) {
        LOGGER.info("this Node is not leader now, oldTerm: {}; will stop debezium", oldTerm);
        //停止DebeziumServerBootstrap
        debeziumServerBootstrapMap.forEach((k, v) -> {
            v.stop();
        });
    }
}
