package net.engining.metrics.support;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-17 17:58
 * @since :
 **/
public class MeterDto implements Serializable {

    private String name;

    private List<Tag> tags;

    private Meter.Type type;

    Iterable<Measurement> measurements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Meter.Type getType() {
        return type;
    }

    public void setType(Meter.Type type) {
        this.type = type;
    }

    public Iterable<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Iterable<Measurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MeterDto.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("tags=" + tags)
                .add("type=" + type)
                .add("measurements=" + measurements)
                .toString();
    }
}
