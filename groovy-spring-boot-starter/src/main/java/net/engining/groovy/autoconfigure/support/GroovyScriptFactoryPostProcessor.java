/*
 * Copyright 2021 caiqy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.engining.groovy.autoconfigure.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;

/**
 * bean后置处理器
 * 扩展部分官方实现
 *
 * @author Eric Lu
 */
public class GroovyScriptFactoryPostProcessor extends ScriptFactoryPostProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(GroovyScriptFactoryPostProcessor.class);

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        try {
            return super.postProcessBeforeInstantiation(beanClass, beanName);
        } catch (BeanCreationException e) {
            // just give a warning message before return null
            LOGGER.warn(e.getMessage());
            return null;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    protected ScriptSource getScriptSource(String beanName, String scriptSourceLocator) {
        // add self protocol here
        return super.getScriptSource(beanName, scriptSourceLocator);
    }
}
