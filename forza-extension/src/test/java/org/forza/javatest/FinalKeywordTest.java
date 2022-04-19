package org.forza.javatest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FinalKeywordTest {

    @Test
    public void testFinal() throws Exception {
        final Integer i = 1001;
        Integer ret = func(i);
        // 方法入参不是final可以改变类型
        System.out.println("ret = " + ret);
    }

    private Integer func(Integer i) {
        i = 2;
        return i;
    }

}
