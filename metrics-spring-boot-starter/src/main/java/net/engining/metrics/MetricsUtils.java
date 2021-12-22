package net.engining.metrics;

import com.google.common.collect.Lists;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import net.engining.metrics.support.MeterDto;
import net.engining.metrics.support.MetricsRepositoriesService;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-22 9:39
 * @since :
 **/
public class MetricsUtils {

    public static final String COMPOSITE_METER_REGISTRY = "compositeMeterRegistry";

    public static void storing(MeterRegistry meterRegistry, MetricsRepositoriesService repositoriesService) {
        List<MeterDto> filteredMeters = Lists.newArrayList();
        meterRegistry.getMeters()
                .forEach(meter -> {
                    String name = meter.getId().getName();
                    MeterDto meterDto = new MeterDto();
                    meterDto.setName(name);
                    meterDto.setTags(meter.getId().getTags());
                    meterDto.setType(meter.getId().getType());
                    meterDto.setMeasurements(meter.measure());
                    filteredMeters.add(meterDto);
                });
        //持久化
        repositoriesService.batchSave(filteredMeters);
    }

    public static MeterRegistry determinedMeterRegistry(ConfigurableApplicationContext applicationContext) {
        //获取容器内已实例化的MeterRegistry
        //注意：MeterRegistryPostProcessor已将Spring容器中的所有MeterRegistry加入Metrics.globalRegistry，
        //但存在CompositeMeterRegistry时Metrics.globalRegistry中会存在两层CompositeMeterRegistry，内层的是Spring容器管理的；
        //如：Metrics.globalRegistry.getRegistries()
        //      0 = {CompositeMeterRegistry@9608}
        //      1 = {StoredStepMeterRegistry@9613}
        //      2 = {StoredPushMeterRegistry@7808}
        //      3 = {PrometheusMeterRegistry@9634}
        //而：CompositeMeterRegistry@9608.getRegistries()
        //      0 = {StoredStepMeterRegistry@9613}
        //      1 = {StoredPushMeterRegistry@7808}
        //      2 = {PrometheusMeterRegistry@9634}
        if (ValidateUtilExt.isNotNullOrEmpty(applicationContext.getBean(COMPOSITE_METER_REGISTRY))){
            return (CompositeMeterRegistry) applicationContext.getBean(COMPOSITE_METER_REGISTRY);
        }
        else {
            return Metrics.globalRegistry;
        }
    }

}
