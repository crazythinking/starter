package net.engining.kettle.prop;

import com.google.common.collect.Maps;
import net.engining.kettle.common.KettleContextInfo;
import net.engining.kettle.common.KettleLogLevelEnum;
import net.engining.kettle.common.KettleTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Kettle 公共参数
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 14:42
 * @since 1.0
 */
@ConfigurationProperties("pg.kettle")
public class KettleCommonProperties {
    /**
     * kettle 是否启动
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
