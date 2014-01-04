package org.tianshan.ds;

public class Message {
	public final static int MSG_IN = 0;
	public final static int MSG_OUT = 1;
	public final static int MSG_NEWIN = 3;
	public final static int MSG_NEWOUT = 4;
	public final static int MSG_ASK = 5;
	public final static int MSG_REPLAY = 6;
	
	private int type;
	
	private int timestamp = 0;
	
	private int port = 0;
	
	private int inNum = 0;
	
	private int outNum = 0;
	
	/**
	 * replay message
	 * @param port
	 * @param inNum
	 * @param outNum
	 */
	public Message(int port, int timestamp, int inNum, int outNum) {
		type = MSG_REPLAY;
		this.port = port;
		this.timestamp = timestamp;
		this.inNum = inNum;
		this.outNum = outNum;
	}
	
	/**
	 * ask for permit message
	 * or car in & car out message
	 * @param port
	 */
	public Message(int type, int port, int timestamp) {
		switch(type) {
		case MSG_IN:
		case MSG_OUT:
		case MSG_ASK:
			this.type = type;
			this.port = port;
			this.timestamp = timestamp;
			break;
		default:
			throw new IllegalArgumentException("Error:message type");
		}
	}
	
	public Message(String msg) {
		String[] attr = msg.split("@");
		
//		if (attr.length < 3) {
//			throw new RuntimeException("Error: msg num ");
//		}
		
		this.type = Byte.parseByte(attr[0]);
		this.port = Integer.parseInt(attr[1]);
		this.timestamp = Integer.parseInt(attr[2]);
		this.inNum = Integer.parseInt(attr[3]);
		this.outNum = Integer.parseInt(attr[4]);
		
	}
	
	public void setTimestamp(int time) {
		timestamp = time;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	public int getType() {
		return type;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getInNum() {
		return inNum;
	}
	
	public int getOutNum() {
		return outNum;
	}
	
	public String toString() {
		String str = String.valueOf((int)type)+"@"+
				String.valueOf(port)+"@"+
				String.valueOf(timestamp)+"@"+
				String.valueOf(inNum)+"@"+
				String.valueOf(outNum);
		return str;
	}
	
	public byte[] toBytes() {
		return toString().getBytes();
	}
}
