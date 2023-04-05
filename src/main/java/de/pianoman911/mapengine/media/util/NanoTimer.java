package de.pianoman911.mapengine.media.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class NanoTimer {

    private static final List<NanoTimer> NANO_TIMERS = new ArrayList<>();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (NanoTimer nanoTimer : NANO_TIMERS) {
                nanoTimer.stop();
            }
        }, "NanoTimer-ShutdownHook"));
    }

    private boolean running = true;

    public NanoTimer(Runnable runnable, double millisDelay, double millisPeriod) {
        EXECUTOR.execute(() -> {
            try {
                LockSupport.parkNanos((long) (millisDelay * 1000000.0));
                while (running) {
                    long timestamp = System.nanoTime();
                    runnable.run();
                    long offset = System.nanoTime() - timestamp;
                    LockSupport.parkNanos((long) (millisPeriod * 1000000.0 - offset));
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        NANO_TIMERS.add(this);
    }

    public void stop() {
        running = false;
    }
}
