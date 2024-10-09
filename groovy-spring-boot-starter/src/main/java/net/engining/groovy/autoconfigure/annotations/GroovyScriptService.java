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

package net.engining.groovy.autoconfigure.annotations;

import org.springframework.core.io.ProtocolResolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * groovy 动态语言component
 *
 * @author Eric Lu
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroovyScriptService {

    /**
     * 脚本资源路径； <br>
     * 支持spring官方协议(eg. classpath:xxx.groovy, file:xxx.groovy) <br>
     * 支持自定义协议(eg. myprotocol:xxx.groovy), 需要实现对应协议的{@link ProtocolResolver} <br>
     * 支持基于占位符“${...}”的外部化配置 <br>
     * 可通过“,”分割，表示多个脚本资源，通常用于接口的多个实现类
     */
    String scriptSources() default "";

    /**
     * 更新检查延时毫秒数, 默认60秒检查一次
     */
    String refreshCheckDelay() default "60000L";


}
