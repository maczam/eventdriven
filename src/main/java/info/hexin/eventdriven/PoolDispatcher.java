package info.hexin.eventdriven;

import info.hexin.eventdriven.event.EventSource;
import info.hexin.eventdriven.listener.EventListener;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 派发线程池
 * 
 * @author RandySuh
 * 
 */
public class PoolDispatcher {
    private static Logger log = LoggerFactory.getLogger(PoolDispatcher.class);

    private volatile boolean started;
    private final EventListener eventListener;
    private Queue<EventSource> eventQueue;

    /** 线程池最小个数 */
    private int minSize = 1;
    /** 线程池最大个数 */
    private int maxSize = 32;
    /** 事件处理超时告警基值 */
    private int timeouts = 3;
    /**
     * 线程池名字，以便监控该线程池的执行状况。
     */
    private String name = "threadpool";

    private ThreadPoolExecutor threadPool;

    public PoolDispatcher(EventListener eventlsnr, Queue<EventSource> eventQueue) {
        this.eventListener = eventlsnr;
        this.eventQueue = eventQueue;
    }

    public void start(boolean userPolicy) {
        if (isStarted())
            return;
        // 启动线程池
        this.threadPool = new ThreadPoolExecutor(minSize, maxSize, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(minSize * 50), new WorkerThreadFactory(name));
        if (userPolicy) {
            this.threadPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                    eventQueue.add(((EventTask) r).getEventSource());
                }
            });

            // 启动线程
            Thread t = new WorkThread();
            t.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            t.start();
        } else {
            this.threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        }

        setStarted(true);
        log.info("线程池(" + name + ")启动, 最小线程数=" + minSize + ", 最大线程数=" + maxSize);
    }

    public final void dispatch(EventSource es) {
        this.threadPool.execute(new EventTask(es));
    }

    public void stop() {
        if (!isStarted())
            return;
        setStarted(false);

        this.threadPool.shutdown();
        try {
            this.threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("线程池(" + name + ")停止。");
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    /** 分发事件内部执行线程类 */
    private final class EventTask implements Runnable {
        private final EventSource es;

        public EventSource getEventSource() {
            return this.es;
        }

        private EventTask(EventSource es) {
            this.es = es;
        }

        public final void run() {
            long beginTime = System.currentTimeMillis();
            eventListener.dispatch(es);
            long endTime = System.currentTimeMillis();

            if (endTime - beginTime > timeouts * 1000) {
                log.warn("Event-(" + es.getEventType().getDescription() + ")处理超时, CostTime(ms)="
                        + (endTime - beginTime));
            }
        }
    }

    /** EventQueue监控线程 */
    private class WorkThread extends Thread {
        EventSource currentEvent = null;

        public WorkThread() {
            super(PoolDispatcher.this.name + "-WorkThread");
        }

        public void run() {
            while (PoolDispatcher.this.isStarted()) {
                try {
                    // 取出待处理事件
                    currentEvent = PoolDispatcher.this.eventQueue.poll();
                    if (currentEvent == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    dispatch(currentEvent);
                } catch (Exception e) {
                    continue;
                }
            }
        }

        private void dispatch(EventSource es) {
            long beginTime = System.currentTimeMillis();
            PoolDispatcher.this.eventListener.dispatch(es);
            long endTime = System.currentTimeMillis();

            if (endTime - beginTime > timeouts * 1000) {
                log.warn("Event-(" + es.getEventType().getDescription() + ")处理超时, CostTime(ms)="
                        + (endTime - beginTime));
            }

        }

    }

    /** 线程异常捕获 */
    class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Thread-" + t.getName(), e);
            Thread next = new WorkThread();
            next.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            next.start();
        }
    }

    public int getPoolQueueRemaining() {
        return threadPool.getQueue().remainingCapacity();
    }

    public int getPoolSize() {
        return threadPool.getPoolSize();
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setTimeouts(int timeouts) {
        this.timeouts = timeouts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
