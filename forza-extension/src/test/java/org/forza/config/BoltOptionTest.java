package org.forza.config;

import org.forza.common.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

/**
 * 选项测试
 */
@RunWith(JUnit4.class)
public class BoltOptionTest {
    @Test
    public void test() {

        ForzaOptions options = new ForzaOptions();
        options.option(ForzaRemotingOption.SERIALIZATION, Constants.DEFAULT_REMOTING_SERIALIZATION);

        Map<String, Object> map = options.options(ForzaRemotingOption.class);

//        map.forEach((k, v) -> {
//            System.out.println(k.name() + ":" + k.defaultValue());
//            System.out.println(v);
//        });
//
//        map = options.options();
//
//        map.forEach((k, v) -> {
//            System.out.println(k.name() + ":" + k.defaultValue());
//            System.out.println(v);
//        });


    }

    @Test
    public void BitTest() throws Exception {
        System.out.println(9&2);
    }
}
