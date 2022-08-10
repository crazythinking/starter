package net.engining.drools.autoconfigure.props;

import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.management.GAV;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-11-17 12:07
 * @since :
 **/
@ConfigurationPropertiesBinding
public class GavConverter implements Converter<String, GAV> {

    @Override
    public GAV convert(String source) {
        return getGav(source);
    }

    public static GAV getGav(String source) {
        List<String> gav = StrSpliter.split(source, ":", true, true);
        return new GAV(gav.get(0), gav.get(1), gav.get(2));
    }
}
