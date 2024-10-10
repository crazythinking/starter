package net.engining.groovy.autoconfigure.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;

/**
 * 支持从Nacos配置中心中读取脚本
 *
 * @author Eric Lu
 * @date 2024-10-08 11:12
 **/
public class NacosProtocolResolver implements ProtocolResolver {

    // Nacos配置服务
    private final ConfigService configService;

    public NacosProtocolResolver(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        //nacos协议，即nacos://group/dataId
        if (location.startsWith("nacos://")) {
            // 从格式为nacos://group/dataId，根据“/”分隔，提取出dataId，group;
            String[] parts = location.substring(8).split("/");
            String group = parts[0];
            String dataId = parts[1];

            try {
                String content = configService.getConfig(dataId, group, 5000);
                return new ByteArrayResource(
                        content.getBytes(StandardCharsets.UTF_8),
                        StrUtil.format("Nacos Config resource [{}/{}]", group, dataId)
                );
            } catch (NacosException e) {
                throw new RuntimeException(StrUtil.format("can not get script from {}", location), e);
            }
        }
        return null;
    }
}
