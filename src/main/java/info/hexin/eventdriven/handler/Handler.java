package info.hexin.eventdriven.handler;

import info.hexin.eventdriven.event.EventSource;

/**
 * 事件处理器
 * 
 * @author RandySuh
 * 
 */
public interface Handler {
	/** 处理事件接口方法 */
	public void action(EventSource es);
}
