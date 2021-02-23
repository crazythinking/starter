package net.engining.datasource.autoconfigure.autotest.jdbc.support;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-12-30 16:14
 * @since :
 **/
public class OperAdtLogPropertyAccessor implements PersistentPropertyAccessor<OperAdtLog> {
    @Override
    public void setProperty(PersistentProperty property, Object value) {

    }

    @Override
    public Object getProperty(PersistentProperty<?> property) {
        return null;
    }

    @Override
    public OperAdtLog getBean() {
        return null;
    }
}
