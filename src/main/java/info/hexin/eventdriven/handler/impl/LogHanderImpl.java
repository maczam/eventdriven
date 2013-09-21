package info.hexin.eventdriven.handler.impl;


import info.hexin.eventdriven.event.EventSource;
import info.hexin.eventdriven.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 日志操作实现
 * @author ydhexin
 * 
 */
public class LogHanderImpl implements Handler {

	private static final Logger logger = LoggerFactory.getLogger(LogHanderImpl.class);
	@Override
	public void action(EventSource es) {
		String message = (String)es.getAttribute("message");
		logger.debug(" 日志操作:"+message);
	}
}
