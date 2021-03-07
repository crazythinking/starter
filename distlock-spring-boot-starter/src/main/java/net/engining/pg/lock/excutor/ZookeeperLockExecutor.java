package net.engining.pg.lock.excutor;

import net.engining.pg.lock.executor.AbstractLockExecutor;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁zookeeper处理器
 *
 * @author Eric Lu
 */
public class ZookeeperLockExecutor extends AbstractLockExecutor<InterProcessMutex> {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(ZookeeperLockExecutor.class);
    private static final String NODE_PATH = "/distlock/%s";

    private final CuratorFramework curatorFramework;

    public ZookeeperLockExecutor(final CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    @Override
    public InterProcessMutex acquire(String lockKey, String lockValue, long expire, long acquireTimeout) {
        if (!CuratorFrameworkState.STARTED.equals(curatorFramework.getState())) {
            log.warn("zookeeper curatorFramework instance must be started before calling this method");
            return null;
        }

        try {
            InterProcessMutex mutex = new InterProcessMutex(curatorFramework, String.format(NODE_PATH, lockKey));
            final boolean locked = mutex.acquire(acquireTimeout, TimeUnit.MILLISECONDS);
            return obtainLockInstance(locked, mutex);
        } catch (Exception e) {
            ExceptionUtilsExt.dump(e);
            return null;
        }
    }

    @Override
    public boolean releaseLock(String key, String value, InterProcessMutex lockInstance) {
        try {
            lockInstance.release();
        } catch (Exception e) {
            log.warn("zookeeper lock release error", e);
            return false;
        }
        return true;
    }

}