package net.engining.minio.autoconfigure;

import net.engining.pg.storage.minio.operators.ObjectOperations;
import net.engining.pg.storage.minio.web.MinioObjectHandlerMethodReturnValueHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@ConditionalOnProperty(prefix = "pg.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureAfter(MinioCoreAutoConfiguration.class)
class MinioWebAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private ObjectOperations objectOperations;

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new MinioObjectHandlerMethodReturnValueHandler(objectOperations));
    }

}
