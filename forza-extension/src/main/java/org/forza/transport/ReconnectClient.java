package org.forza.transport;


import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.forza.common.Url;
import org.forza.reomoting.AbstractLifeCycle;
import org.forza.util.NamedThreadFactory;
import org.forza.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author:  
 * @DateTime: 2020/5/5
 * @Description: TODO
 */
public class ReconnectClient extends AbstractLifeCycle {
    private static final Logger logger = LoggerFactory.getLogger(ReconnectClient.class);
    private static final HashedWheelTimer IDLE_CHECK_TIMER = new HashedWheelTimer(
            new NamedThreadFactory("bolt-client-reconnect-timer", true), 1, TimeUnit.SECONDS);
    private LinkedBlockingQueue<ReconnectTimerTask> tasks = new LinkedBlockingQueue<ReconnectTimerTask>();
    private Client client;
    private Thread reconnectThread;

    public ReconnectClient(Client client) {
        this.client = client;
    }

    @Override
    public void startUp() {
        super.startUp();
        this.reconnectThread = new Thread(() -> {
            while (isStarted()) {
                ReconnectTimerTask task = null;
                try {
                    task = tasks.take();
                } catch (InterruptedException e) {
                }
                IDLE_CHECK_TIMER.newTimeout(task, task.getReconnectCount() * 3, TimeUnit.SECONDS);
            }
        }, "bolt-client-reconnect-thread");
        reconnectThread.setDaemon(true);
        reconnectThread.start();
    }

    @Override
    public void shutDown() {
        super.shutDown();
        tasks.clear();
        IDLE_CHECK_TIMER.stop();
    }

    public void reconnect(Url url) {
        try {
            tasks.put(new ReconnectTimerTask(url));
        } catch (InterruptedException e) {
            logger.warn(e.getMessage(), e);
        }
    }


    private class ReconnectTimerTask implements TimerTask {
        private final Url url;
        private AtomicInteger reconnectCount = new AtomicInteger(0);

        private ReconnectTimerTask(Url url) {
            this.url = url;
        }

        public ReconnectTimerTask addReconnectCount() {
            reconnectCount.getAndIncrement();
            return this;
        }

        protected int getReconnectCount() {
            return reconnectCount.get();
        }

        @Override
        public String toString() {
            return "ReconnectTask{" +
                    "url=" + url +
                    ", reconnectCount=" + reconnectCount +
                    '}';
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Reconnect to server count " + getReconnectCount() + ", client(url: " + url + ")");
                }
                client.ctreateConnectionIfAbsent(url);
            } catch (Exception e) {
                logger.warn(ObjectUtils.toString(e));
                tasks.put(addReconnectCount());
            }
        }
    }


}
