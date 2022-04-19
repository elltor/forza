package org.forza.javatest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IntegerTest {

    @Test
    public void t() throws Exception {
        int i = 1001;
        Integer i2 = new Integer(1001);
//        int i3 = i2.intValue();
        System.out.println(i == i2);
    }
}
