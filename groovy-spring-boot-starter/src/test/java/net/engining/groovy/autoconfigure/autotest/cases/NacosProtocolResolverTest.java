package net.engining.groovy.autoconfigure.autotest.cases;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import net.engining.groovy.autoconfigure.support.NacosProtocolResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NacosProtocolResolverTest{

    private ConfigService configService;

    private NacosProtocolResolver nacosProtocolResolver;

    @BeforeEach
    void setUp() {
        configService = mock(ConfigService.class);
        nacosProtocolResolver = new NacosProtocolResolver(configService);
    }

    @Test
    void testResolve_withNacosProtocol_throwsException() throws NacosException {
        String location = "nacos://testGroup/testDataId";
    
        when(configService.getConfig("testDataId", "testGroup", 5000)).thenThrow(new NacosException());
    
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            nacosProtocolResolver.resolve(location, mock(ResourceLoader.class));
        });
    
        assertTrue(exception.getCause() instanceof NacosException);
    }

    @Test
    void testResolve_withNonNacosProtocol() {
        String location = "http://example.com/resource";
    
        Resource resource = nacosProtocolResolver.resolve(location, mock(ResourceLoader.class));
    
        assertNull(resource);
    }

    @Test
    void testResolve_withNacosProtocol() throws NacosException, IOException {
        String location = "nacos://testGroup/testDataId";
        String expectedConfig = "testConfigContent";
    
        when(configService.getConfig("testDataId", "testGroup", 5000)).thenReturn(expectedConfig);
    
        Resource resource = nacosProtocolResolver.resolve(location, mock(ResourceLoader.class));
    
        assertNotNull(resource);
        try (InputStream inputStream = resource.getInputStream()) {
            String actualConfig = new String(toByteArray(inputStream, resource.contentLength()));
            assertEquals(expectedConfig, actualConfig);
        }

    }

    private static byte[] toByteArray(InputStream inputStream, long length) throws IOException {
        // 使用 ByteArrayOutputStream 来累积字节
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[(int) length];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

}