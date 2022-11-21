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
     * bean 名称
     */
    String id() default "";

    /**
     * 脚本资源路径
     * 支持spring官方协议(eg. classpath:xxx.groovy)
     * 支持自定义协议 database: redis:
     */
    String scriptSource() default "";

    /**
     * 更新检查延时
     */
    long refreshCheckDelay() default -1L;

    /**
     * groovy 自定义 bean 名称
     */
    String customizerRef() default "";

}
