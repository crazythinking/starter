package net.engining.rheakv.autoconfigure.facility;

import com.alipay.sofa.jraft.rhea.StateListener;
import org.springframework.context.ApplicationContext;

public interface StateListener4Spring extends StateListener {

    void setRegionId(Long regionId);

    Long getRegionId();

    void setApplicationContext(ApplicationContext applicationContext);

}
