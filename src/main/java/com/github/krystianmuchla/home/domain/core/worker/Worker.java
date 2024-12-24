package com.github.krystianmuchla.home.domain.core.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;

public abstract class Worker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private final Duration rate;

    protected Worker(Duration rate) {
        this.rate = rate;
    }

    @Override
    public void run() {
        while (true) {
            try {
                work();
            } catch (Exception exception) {
                LOG.warn("{}", exception.getMessage(), exception);
            }
            try {
                Thread.sleep(rate);
            } catch (InterruptedException exception) {
                LOG.info("{}", exception.getMessage(), exception);
            }
        }
    }

    abstract protected void work();

    public static void start(Collection<Worker> workers) {
        for (var worker : workers) {
            Thread.startVirtualThread(worker);
        }
    }
}
