package org.cloud.gateway.transport.netty;

import com.google.inject.Singleton;
import com.netflix.config.DynamicIntProperty;

/**
 * Created by cjy on 2020/1/24.
 */
@Singleton
public class DefaultEventLoopConfig implements EventLoopConfig
{
    private static final DynamicIntProperty ACCEPTOR_THREADS =
            new DynamicIntProperty("zuul.server.netty.threads.acceptor", 1);
    private static final DynamicIntProperty WORKER_THREADS =
            new DynamicIntProperty("zuul.server.netty.threads.worker", -1);
    private static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

    private final int eventLoopCount;
    private final int acceptorCount;

    public DefaultEventLoopConfig()
    {
        eventLoopCount = WORKER_THREADS.get() > 0 ? WORKER_THREADS.get() : PROCESSOR_COUNT;
        acceptorCount = ACCEPTOR_THREADS.get();
    }

    public DefaultEventLoopConfig(int eventLoopCount, int acceptorCount)
    {
        this.eventLoopCount = eventLoopCount;
        this.acceptorCount = acceptorCount;
    }

    @Override
    public int eventLoopCount()
    {
        return eventLoopCount;
    }

    @Override
    public int acceptorCount()
    {
        return acceptorCount;
    }
}
