package info.hexin.eventdriven.listener.impl;

import info.hexin.eventdriven.PoolDispatcher;
import info.hexin.eventdriven.event.EventSource;
import info.hexin.eventdriven.event.EventType;
import info.hexin.eventdriven.handler.Handler;
import info.hexin.eventdriven.lang.LinkedTransferQueue;
import info.hexin.eventdriven.listener.EventListener;

import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;

public class SimpleEventListener implements EventListener {

	/** 线程池最小个数 */
	protected int minSize = 3;
	/** 线程池最大个数 */
	protected int maxSize = 16;
	/** 事件处理超时告警基值 */
	protected int timeouts = 3;
	/** 事件监听器名称 */
	protected String listenerName = "SimpleEventlsnr";
	/** 线程池处理忙不过来时使用自定义工作线程策略还是系统ThreadPoolExecutor.CallerRunsPolicy()策略 */
	protected boolean userPolicy = true;

	protected volatile boolean started;
	protected Map<EventType, Handler> handlers = new EnumMap<EventType, Handler>(EventType.class);

	protected PoolDispatcher pool = null;
	protected Queue<EventSource> eventQueue = new LinkedTransferQueue<EventSource>();

	public SimpleEventListener(int minSize) {
		this.minSize = minSize;
	}

	protected void setStarted(boolean started) {
		this.started = started;
	}

	/** 事件分发到相应的Handler处理 */
	public void dispatch(EventSource es) {
		Handler handler = handlers.get(es.getEventType());
		if (handler != null)
			handler.action(es);
	}

	public boolean isStarted() {
		return this.started;
	}

	/** 事件入队 */
	public void onEvent(EventSource es) {
		if (!isStarted())
			return;
		pool.dispatch(es);
	}

	/** 在事件监听器上注册事件和对应的处理Handler */
	public void registerEvent(EventType event, Handler handler) {
		if (handler != null)
			handlers.put(event, handler);
	}

	protected void startThreadPool() {
		pool = new PoolDispatcher(this, eventQueue);
		pool.setMinSize(minSize);
		pool.setMaxSize(maxSize);
		pool.setTimeouts(timeouts);
		pool.setName(listenerName + "Pool");

		pool.start(userPolicy);
	}

	/** 启动事件监听器 */
	public void start() {
		if (isStarted())
			return;

		startThreadPool();
		setStarted(true);
	}

	/** 停止事件监听器 */
	public void stop() {
		if (!isStarted())
			return;

		setStarted(false);

		// 让事件处理完毕
		int cnt = 100;
		while (cnt-- > 0 && eventQueue.size() > 0) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
		}

		// 从监听管理器中注销事件监听器
		// EventLsnrManager.unregisterEventListener(this);

		if (null != pool)
			pool.stop();
		pool = null;
	}

	public void unregisterEvent(EventType event, Handler handler) {
		handlers.remove(event);
	}

	public EventType[] getRegisterEvents() {
		EventType[] o = new EventType[handlers.size()];
		return handlers.keySet().toArray(o);
	}

	public int getMinSize() {
		return minSize;
	}

	public void setUserPolicy(boolean userPolicy) {
		this.userPolicy = userPolicy;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getTimeouts() {
		return timeouts;
	}

	public void setTimeouts(int timeouts) {
		this.timeouts = timeouts;
	}

	public String getListenerName() {
		return listenerName;
	}

	public void setListenerName(String name) {
		this.listenerName = name;
	}
}
