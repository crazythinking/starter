package net.engining.pg.disruptor.autoconfigure.autotest.cases;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2023-06-27 16:20
 **/
public class SimpleTestCase {

    @Test
    @Benchmark
    public void testSimple2P2C() {
         SimpleTest test = new SimpleTest();
         test.testSimple2P2C();
    }

    @Test
    @Benchmark
    public void testSimple2PmC() {
        SimpleTest test = new SimpleTest();
        test.testSimple2PmC();
    }
}
