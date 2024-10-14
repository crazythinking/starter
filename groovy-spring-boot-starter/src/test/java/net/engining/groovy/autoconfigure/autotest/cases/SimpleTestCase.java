package net.engining.groovy.autoconfigure.autotest.cases;


import cn.hutool.core.lang.Console;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.engining.groovy.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.groovy.autoconfigure.autotest.support.Gamer;
import net.engining.groovy.autotest.services.UserService;
import net.engining.groovy.autotest.services2.UserService2;
import net.engining.groovy.autotest.services3.UserService3;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.syntax.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Eric Lu
 **/
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Autowired(required = false)
    private List<UserService> userServices;

    @Resource(name = "UserServiceImpl3.groovy")
    private UserService userService;

    @Autowired(required = false)
    private UserService2 userService2;

    @Autowired
    Gamer gamer;

    @Override
    public void initTestData() {

    }

    @Override
    public void assertResult() {
    }

    @Override
    public void testProcess() throws IOException, ScriptException, ResourceException {

        if (ValidateUtilExt.isNotNullOrEmpty(userServices)) {
            userServices.forEach(service -> LOGGER.debug(service.hello("dynamic language")));
        }

        if (userService2 != null) {
            LOGGER.debug(userService2.hello("dynamic language"));
        }

        useGroovyScriptEngine();

        if (userService != null) {
            LOGGER.debug(userService.hello("dynamic language"));
        }

        ApplicationContextHolder.getBeansOfType(UserService.class).forEach((key, value) -> {
            LOGGER.debug(key);
            LOGGER.debug(value.hello("dynamic language"));
        });

    }

    //不推荐直接使用GroovyScriptEngine
    private void useGroovyScriptEngine() throws IOException, ResourceException, ScriptException {
        CompilerConfiguration config = new CompilerConfiguration();
        // 创建SecureASTCustomizer
        final SecureASTCustomizer secure = new SecureASTCustomizer();
        // 禁止使用闭包
        secure.setClosuresAllowed(true);
        List<Integer> tokensBlacklist = new ArrayList<>();
        // 添加关键字黑名单 while和goto
        tokensBlacklist.add(Types.KEYWORD_WHILE);
        tokensBlacklist.add(Types.KEYWORD_GOTO);
        secure.setDisallowedTokens(tokensBlacklist);
        // 设置直接导入检查
        secure.setIndirectImportCheckEnabled(true);
        // 添加导入黑名单，用户不能导入JSONObject
        List<String> list = new ArrayList<>();
        list.add("com.alibaba.fastjson.JSONObject");
        secure.setDisallowedImports(list);
        // statement 黑名单，不能使用while循环块
        List<Class<? extends Statement>> statementBlacklist = new ArrayList<>();
        statementBlacklist.add(WhileStatement.class);
        secure.setDisallowedStatements(statementBlacklist);
        config.addCompilationCustomizers(secure);
        //config.setMinimumRecompilationInterval(100);默认100
        //config.setClasspath("D:\\idea-workspace\\power-gears\\starter\\groovy\\jars\\groovy-deps-test-1.0-SNAPSHOT.jar");

        //动态添加classpath这里有坑，不能通过config添加，需要直接通过ClassLoader添加，原因未确定
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(
                this.getClass().getClassLoader(),
                config,
                true
        );
        if (groovyClassLoader.getParent() instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader)groovyClassLoader.getParent()).getURLs();
            Arrays.stream(urls).forEach(Console::log);
        }

        //可以通过添加-classpath参数的方式添加
        //groovyClassLoader.addClasspath("D:\\idea-workspace\\power-gears\\starter\\groovy\\script1\\jars\\groovy-deps-test-1.0-SNAPSHOT.jar");

        GroovyScriptEngine engine = new GroovyScriptEngine(
                "file:///D:\\idea-workspace\\power-gears\\starter\\groovy\\script1\\",
                groovyClassLoader
        );

        Binding binding1 = new Binding();
        binding1.setProperty("gamer", gamer);
        //注意：使用GroovyScriptEngine运行脚本，脚本文件名不能与脚本中的类名相同
        //每次groovy脚本改变，其内部会生成新的Class对象；每次调用run，返回的对象也是新的；因此有内存泄漏的风险
        UserService3 obj = (UserService3) engine.run("UserService3ImplScript.groovy", binding1);
        obj.hello("dynamic language");
        LOGGER.debug(obj.toString());

        obj = (UserService3) engine.run("UserService3ImplScript.groovy", binding1);
        obj.hello("dynamic language");
        LOGGER.debug(obj.toString());
    }

    @Override
    public void end() {

    }

}
