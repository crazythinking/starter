package net.engining.debezium.facility;

import com.alipay.sofa.jraft.core.NodeImpl;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.rhea.JRaftHelper;
import com.alipay.sofa.jraft.rhea.client.DefaultRheaKVStore;
import io.debezium.engine.DebeziumEngine;
import net.engining.pg.storage.rheakv.KvServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * 当 DebeziumEngine connector stop 时，主动切换JRaft leader；使其他多活节点能够主动转变为Leader
 *
 * @author luxue
 */
public class LeaderShiftConnectorCallback implements DebeziumEngine.ConnectorCallback {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderShiftConnectorCallback.class);

    private final Long regionId;
    private final ApplicationContext applicationContext;

    public LeaderShiftConnectorCallback(Long regionId, ApplicationContext applicationContext) {
        this.regionId = regionId;
        this.applicationContext = applicationContext;
    }

    @Override
    public void connectorStopped() {
        //shifting the JRaft leader
        transferLeader();
        LOGGER.warn("DebeziumEngine connector stopped, has bean shifting the JRaft leader");
    }

    public void transferLeader() {
        KvServer kvServer = applicationContext.getBean(KvServer.class);
        DefaultRheaKVStore defaultRheaKVStore = (DefaultRheaKVStore) kvServer.getRheaKVStore();
        if (!defaultRheaKVStore.isLeader(regionId)) {
            LOGGER.warn("The current node is not the leader of region {}", regionId);
            return;
        }

        final PeerId leader = defaultRheaKVStore.getStoreEngine().getRegionEngine(regionId).getLeaderId();
        NodeImpl node = (NodeImpl) defaultRheaKVStore.getStoreEngine().getRegionEngine(regionId).getNode();
        final Set<PeerId> peers = node.getCurrentConf().getPeerSet();
        PeerId targetPeer = null;
        for (final PeerId peer : peers) {
            if (!peer.equals(leader)) {
                targetPeer = peer;
                break;
            }
        }
        if (targetPeer == null) {
            LOGGER.warn("No peer to transfer leader");
            return;
        }
        boolean s = defaultRheaKVStore.getPlacementDriverClient().transferLeader(
                regionId,
                JRaftHelper.toPeer(targetPeer),
                true
        );
        if (!s) {
            LOGGER.warn("Transfer leader failed");
        }

    }
}
