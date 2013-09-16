package info.hexin.eventdriven.listener;

import info.hexin.eventdriven.event.EventSource;
import info.hexin.eventdriven.event.EventType;
import info.hexin.eventdriven.handler.Handler;

public interface EventListener {
	/**
	 * 事件入队
	 * 
	 * @param es
	 */
	public void onEvent(EventSource es);

	/**
	 * 事件分发至Handler
	 * 
	 * @param es
	 */
	public void dispatch(EventSource es);

	public void registerEvent(EventType event, Handler handler);
}
