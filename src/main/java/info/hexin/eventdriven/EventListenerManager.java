package info.hexin.eventdriven;

import info.hexin.eventdriven.event.EventSource;
import info.hexin.eventdriven.listener.EventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventListenerManager {
	private static Logger log = LoggerFactory.getLogger(EventListenerManager.class);
	private static EventListenerManager instance = new EventListenerManager();
	private static ArrayList<EventListener>[] listeners = (ArrayList<EventListener>[]) Array.newInstance(ArrayList.class, 128);
	private static final Object lock = new Object[0];

	private EventListenerManager() {

	}

	public static EventListenerManager getInstance() {
		return instance;
	}

	/** 注册事件监听器 */
//	public static final void registerEventListener(EventListener listener) {
//		synchronized (lock) {
//			if (null == listeners) {
//				listeners = new ArrayList[128];
//				Arrays.fill(listeners, null);
//			}
//			EventType[] events = listener.getRegisterEvents();
//			for (EventType event : events) {
//				if (listeners.length < event.ordinal()) {
//					ArrayList<EventListener>[] t = new ArrayList[event.ordinal() + 1];
//					Arrays.fill(t, null);
//					System.arraycopy(listeners, 0, t, 0, listeners.length);
//					listeners = t;
//				}
//				if (null == listeners[event.ordinal()]) {
//					listeners[event.ordinal()] = new ArrayList<EventListener>();
//				}
//				listeners[event.ordinal()].add(listener);
//			}
//		}
//		listener.start();
//	}

	/** 注销事件监听器 */
//	public static final void unregisterEventListener(EventListener listener) {
//		synchronized (lock) {
//			for (int i = 0; i < listeners.length; i++) {
//				if (listeners[i] != null)
//					listeners[i].remove(listener);
//			}
//		}
//		listener.stop();
//	}

	/** 注销并停止所有监听器 */
//	public static final void stopAllEventListeners() {
//		synchronized (lock) {
//			for (int i = 0; i < listeners.length; i++) {
//				if (listeners[i] != null) {
//					for (EventListener listener : listeners[i]) {
//						listener.stop();
//					}
//					listeners[i].clear();
//				}
//			}
//		}
//	}

	/** 触发事件 */
	public static void onEvent(EventSource es) {
		instance.postEvent(es);
	}

	public final void postEvent(EventSource es) {
		boolean hooked = false;
		ArrayList<EventListener> lsnrs = listeners[es.getEventType().ordinal()];
		if (null != lsnrs && lsnrs.size() > 0) {
			try {
				for (EventListener lsnr : lsnrs) {
					lsnr.onEvent(es);
				}
				hooked = true;
			} catch (Exception e) {
				log.error("EventListener.onEvent:", e);
			}
		}
		if (!hooked) {
			log.info("Event-(" + es.getEventType().getDescription() + ")未注册到事件监听器");
		}
	}

}
