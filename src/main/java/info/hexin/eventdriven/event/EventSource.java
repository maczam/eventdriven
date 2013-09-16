package info.hexin.eventdriven.event;

import java.util.HashMap;

/**
 * 事件类，生成指定类型事件
 * 
 * @author RandySuh
 * 
 */
public class EventSource {
	/** 事件类型 */
	private final EventType eventType;
	/** 事件传递属性 */
	private final HashMap<String, Object> attributes = new HashMap<String, Object>();

	public EventSource(EventType eventType) {
		this.eventType = eventType;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}

	public void removeAttribute(String key) {
		this.attributes.remove(key);
	}

	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	public void clearAttributes() {
		this.attributes.clear();
	}
}
