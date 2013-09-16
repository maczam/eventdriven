package info.hexin.eventdriven.event;

/**
 * 事件类型定义
 * 
 * @author RandySuh
 * 
 */
public enum EventType {

	MSG_RECV("收消息"), MSG_SEND("发送下行报文"), MSG_RESPONSE("响应消息"), RETURN_TASK(
			"任务返回"), MSG_RESPONSE2DB("数据入库");

	EventType(String desc) {
		this.description = desc;
	}

	/** 事件描述 */
	private String description;

	public String getDescription() {
		return this.description;
	}
}
