package org.forza.common.serialize;

import org.forza.serialization.ObjectInput;
import org.forza.serialization.ObjectOutput;
import org.forza.serialization.hessian2.Hessian2Serialization;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@RunWith(JUnit4.class)
public class SerializationTest {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Test
    public void test_se() throws Exception {
        String str = "zhan";
        Data data = new Data();
        data.setName(str);
        Hessian2Serialization hessian2Serialization = new Hessian2Serialization();
        ObjectOutput objectOutput = hessian2Serialization.serialize(byteArrayOutputStream);

        objectOutput.writeUTF(str);
        objectOutput.writeObject(data);

        objectOutput.flushBuffer();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                byteArrayOutputStream.toByteArray());
        ObjectInput objectInput = hessian2Serialization.deserialize(byteArrayInputStream);
        Assert.assertEquals(str, objectInput.readUTF());
        Assert.assertEquals(data.toString(), objectInput.readObject(Data.class).toString());
    }

    @Test
    public void t() {
        boolean a = true;
        boolean b = true;

        if (!a && !b) {
            System.out.println("......");
        }
    }

    @lombok.Data
    public static class Data implements Serializable {
        String name;
    }
}
