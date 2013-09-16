package info.hexin.eventdriven;

import info.hexin.eventdriven.event.EventSource;
import info.hexin.eventdriven.event.EventType;
import info.hexin.eventdriven.handler.Handler;
import info.hexin.eventdriven.handler.impl.LogHanderImpl;
import info.hexin.eventdriven.listener.impl.SimpleEventListener;

import java.util.concurrent.LinkedTransferQueue;

/**
 * 
 * @author ydhexin
 *
 */
public class Main {
	public static void main(String[] args) {
		
		
		SimpleEventListener listener = new SimpleEventListener(10);
		Handler handler = new LogHanderImpl(); 
		listener.registerEvent(EventType.MSG_RECV, handler);
		LinkedTransferQueue<EventSource> eventQueue = new LinkedTransferQueue<EventSource>();
		PoolDispatcher dispatcher = new PoolDispatcher(listener, eventQueue);
		dispatcher.start(true);
		
		for(int i = 0 ; i < 10 ; i ++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dispatcher.dispatch(new EventSource(EventType.MSG_RECV));
			dispatcher.dispatch(new EventSource(EventType.MSG_RESPONSE));
		}
		
		
	}
}
