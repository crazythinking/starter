package net.engining.sftp.autoconfigure.props;

import com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

@ConfigurationProperties(prefix = "pg.sftp.muti")
public class MutiSftpProperties {

    @NestedConfigurationProperty
    private SftpProperties defaultSftpProperties;

    private Map<String, SftpProperties> namedSftpProperties = Maps.newHashMap();

    public SftpProperties getDefaultSftpProperties() {
        return defaultSftpProperties;
    }

    public void setDefaultSftpProperties(SftpProperties defaultSftpProperties) {
        this.defaultSftpProperties = defaultSftpProperties;
    }

    public Map<String, SftpProperties> getNamedSftpProperties() {
        return namedSftpProperties;
    }

    public void setNamedSftpProperties(Map<String, SftpProperties> namedSftpProperties) {
        this.namedSftpProperties = namedSftpProperties;
    }
}
