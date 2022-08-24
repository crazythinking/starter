package net.engining.debezium.facility;

import cn.hutool.core.util.StrUtil;
import net.engining.debezium.autoconfigure.DebeziumAutoConfiguration;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.apache.kafka.connect.storage.OffsetBackingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link OffsetBackingStore} implementation that stores the offset data in Sofa-RheaKV.
 * To ensure this behaves similarly to a real backing store, operations are executed asynchronously on a background thread.
 *
 * @author luxue
 */
public class JRaftOffsetBackingStore extends MemoryOffsetBackingStore {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(JRaftOffsetBackingStore.class);

    private static final String OFFSET_FILE_SUFFIX="2-offsets";
    private static final String DATABASE_SERVER_NAME = DebeziumAutoConfiguration.DATABASE_SERVER_NAME;

    private String key;
    private KvServer kvServer;

    @Override
    public void configure(WorkerConfig config) {
        super.configure(config);
        this.key = config.originals().get(DATABASE_SERVER_NAME)+ StrUtil.DASHED + OFFSET_FILE_SUFFIX;
        //get clientService from ApplicationContextHolder
        this.kvServer = ApplicationContextHolder.getBean(KvServer.class);
    }

    @Override
    public synchronized void start() {
        super.start();
        LOGGER.info("Starting JRaftOffsetBackingStore with the RheaKV {}", kvServer);
        load();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        // Nothing to do since this doesn't maintain any outstanding connections/data
        LOGGER.info("Stopped JRaftOffsetBackingStore");
    }

    private void load() {
        //read data from RheaKV
        byte[] bytes = this.kvServer.getRheaKVStore().bGet(key);
        if (ValidateUtilExt.isNullOrEmpty(bytes)) {
            return;
        }
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
        ) {
            Object obj = objectInputStream.readObject();
            if (!(obj instanceof HashMap))
                throw new ConnectException("Expected HashMap but found " + obj.getClass());
            Map<byte[], byte[]> raw = (Map<byte[], byte[]>) obj;
            for (Map.Entry<byte[], byte[]> mapEntry : raw.entrySet()) {
                ByteBuffer key = (mapEntry.getKey() != null) ? ByteBuffer.wrap(mapEntry.getKey()) : null;
                ByteBuffer value = (mapEntry.getValue() != null) ? ByteBuffer.wrap(mapEntry.getValue()) : null;
                data.put(key, value);
            }
        } catch (Exception e) {
            throw new ConnectException(e);
        }
    }

    @Override
    protected void save() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            Map<byte[], byte[]> raw = new HashMap<>();
            for (Map.Entry<ByteBuffer, ByteBuffer> mapEntry : data.entrySet()) {
                byte[] key = (mapEntry.getKey() != null) ? mapEntry.getKey().array() : null;
                byte[] value = (mapEntry.getValue() != null) ? mapEntry.getValue().array() : null;
                raw.put(key, value);
            }
            //save data to RheaKV
            objectOutputStream.writeObject(raw);
            //阻塞写入可确保顺序
            this.kvServer.getRheaKVStore().bPut(key, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new ConnectException(e);
        }
    }
}
