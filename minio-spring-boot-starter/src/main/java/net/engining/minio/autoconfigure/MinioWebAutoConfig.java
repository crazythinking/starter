package net.engining.minio.autoconfigure;

import net.engining.pg.storage.minio.operators.ObjectOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

@ConditionalOnProperty(prefix = "pg.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureAfter(MinioCoreAutoConfig.class)
class MinioWebAutoConfig implements WebMvcConfigurer {

    @Autowired
    private ObjectOperations objectOperations;

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new MinioObjectHandlerMethodReturnValueHandler(objectOperations));
    }

}
