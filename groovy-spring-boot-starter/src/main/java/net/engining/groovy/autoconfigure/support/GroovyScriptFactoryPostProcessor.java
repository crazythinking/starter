package net.engining.groovy.autoconfigure.support;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.scripting.support.StaticScriptSource;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * bean后置处理器
 * 扩展部分官方实现
 *
 * @author Eric Lu
 */
public class GroovyScriptFactoryPostProcessor extends ScriptFactoryPostProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(GroovyScriptFactoryPostProcessor.class);

    public static final String NACOS_SCRIPT_PREFIX = "nacos:";

    public static final String GROOVY = "groovy";

    public static String beanNameSuffix(String scriptSourceLocator) {
        return StrUtil.subAfter(scriptSourceLocator, "/", true);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.endsWith("."+GROOVY)) {
            LOGGER.trace("script bean:{}", beanName);
        }
        return super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    protected ScriptSource convertToScriptSource(String beanName, String scriptSourceLocator, ResourceLoader resourceLoader) {
        //可通过注入不同的ProtocolResolver实现支持自定义协议来获取脚本
        //like nacos://, redis://, http://, https:// and so on
        if (scriptSourceLocator.startsWith(INLINE_SCRIPT_PREFIX)) {
            return new StaticScriptSource(scriptSourceLocator.substring(INLINE_SCRIPT_PREFIX.length()), beanName);
        }
        else if (scriptSourceLocator.startsWith(NACOS_SCRIPT_PREFIX)) {
            ByteArrayResource resource = (ByteArrayResource) resourceLoader.getResource(scriptSourceLocator);
            String script = new String(resource.getByteArray(), StandardCharsets.UTF_8);
            // 由于从nacos获取的脚本直接是字符串，所以必须设置classname
            return new StaticScriptSource(script, beanName);
        }
        else {
            return new ResourceScriptSource(resourceLoader.getResource(scriptSourceLocator));
        }
    }

    public ScriptSource getScriptSourceByBeanName(String beanNameSuffix) {
        Map<String, ScriptSource> scriptSourceCache =
                (Map<String, ScriptSource>) ReflectUtil.getFieldValue(this, "scriptSourceCache");
        Field field = ReflectUtil.getField(GroovyScriptFactoryPostProcessor.class, "SCRIPT_FACTORY_NAME_PREFIX");
        String prefix = (String) ReflectUtil.getStaticFieldValue(field);
        return scriptSourceCache.get(prefix + beanNameSuffix);
    }

}
