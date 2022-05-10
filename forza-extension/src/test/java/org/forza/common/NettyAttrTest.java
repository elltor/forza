package org.forza.common;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.forza.reomoting.Connection;
import org.forza.util.CountDownLatchUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * forza
 */
@RunWith(JUnit4.class)
public class NettyAttrTest {
    @Test
    public void test() throws InterruptedException {
        AttributeKey<Connection> conKey = AttributeKey.valueOf("CONNECTION");
        io.netty.channel.Channel ch = new NioSocketChannel();

        CountDownLatchUtil latchUtil = new CountDownLatchUtil();

        for (int i = 0; i < 5; i++) {
            Connection con = new Connection(ch);
            System.out.println("插入：" + con);
            System.out.println("ch.hasAttr(conKey) = " + ch.hasAttr(conKey));
            ch.attr(conKey).setIfAbsent(con);
        }


        Connection connection = ch.attr(conKey).get();
        System.out.println(connection);
    }

    @Test
    public void test1(){
        Channel ch = new NioSocketChannel();
        ch.close();
        ch.close();
    }
}
