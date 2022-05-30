package net.engining.debezium.facility;

import cn.hutool.core.util.StrUtil;
import com.alipay.sofa.jraft.util.BytesUtil;
import io.debezium.annotation.ThreadSafe;
import io.debezium.config.Configuration;
import io.debezium.document.DocumentReader;
import io.debezium.document.DocumentWriter;
import io.debezium.relational.history.AbstractDatabaseHistory;
import io.debezium.relational.history.DatabaseHistory;
import io.debezium.relational.history.DatabaseHistoryException;
import io.debezium.relational.history.DatabaseHistoryListener;
import io.debezium.relational.history.HistoryRecord;
import io.debezium.relational.history.HistoryRecordComparator;
import io.debezium.util.FunctionalReadWriteLock;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.debezium.relational.history.KafkaDatabaseHistory.INTERNAL_CONNECTOR_ID;

/**
 * A {@link DatabaseHistory} implementation that stores the schema history in Sofa-RheaKV.
 *
 * @author Eric Lu
 */
@ThreadSafe
public class JRaftDatabaseHistory extends AbstractDatabaseHistory {

    private static final String HISTORY_FILE_SUFFIX="1-history";

    private String key;
    private KvServer kvServer;
    private final FunctionalReadWriteLock lock = FunctionalReadWriteLock.reentrant();
    private final DocumentWriter writer = DocumentWriter.defaultWriter();
    private final DocumentReader reader = DocumentReader.defaultReader();
    private final AtomicBoolean running = new AtomicBoolean();

    @Override
    public void configure(Configuration config, HistoryRecordComparator comparator,
                          DatabaseHistoryListener listener, boolean useCatalogBeforeSchema) {
        if (running.get()) {
            throw new IllegalStateException("JRaft Database history already initialized to " + this.kvServer);
        }
        super.configure(config, comparator, listener, useCatalogBeforeSchema);
        //INTERNAL_CONNECTOR_ID是从配置项DATABASE_SERVER_NAME获取的
        this.key = config.getString(INTERNAL_CONNECTOR_ID.name()) + StrUtil.DASHED + HISTORY_FILE_SUFFIX;
    }

    @Override
    public void start() {
        super.start();
        lock.write(() -> {
            if (running.compareAndSet(false, true)) {
                //get clientService from ApplicationContextHolder
                this.kvServer = ApplicationContextHolder.getBean(KvServer.class);
            }
        });
    }

    @Override
    protected void storeRecord(HistoryRecord record) throws DatabaseHistoryException {
        if (record == null) {
            return;
        }
        lock.write(() -> {
            if (!running.get()) {
                throw new IllegalStateException("The history has been stopped and will not accept more records");
            }
            try {
                String line = writer.write(record.document());
                //阻塞写入可确保顺序
                this.kvServer.getRheaKVStore().bMerge(this.key, line + StrUtil.LF);
            }
            catch (Exception e) {
                logger.error("Failed to add record to history at {}: {}", this.kvServer, record, e);
            }
        });
    }

    @Override
    protected synchronized void recoverRecords(Consumer<HistoryRecord> records) {
        lock.write(() -> {
            try {
                if (exists()) {
                    for (String line : readAllLines()) {
                        if (ValidateUtilExt.isNotNullOrEmpty(line)) {
                            if (line.startsWith(StrUtil.COMMA)) {
                                line = line.substring(1);
                            }
                            records.accept(new HistoryRecord(reader.read(line)));
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error("Failed to add recover records from history at {}", this.kvServer, e);
            }
        });
    }

    private List<String> readAllLines() {
        String value = BytesUtil.readUtf8(this.kvServer.getRheaKVStore().bGet(this.key));
        return StrUtil.split(value, StrUtil.LF);
    }

    @Override
    public void stop() {
        running.set(false);
        super.stop();
    }

    @Override
    public boolean exists() {
        return storageExists();
    }

    @Override
    public boolean storageExists() {
        //检查RheaKV中是否存在对应的history数据
        return kvServer.getRheaKVStore().bContainsKey(key);
    }
}
