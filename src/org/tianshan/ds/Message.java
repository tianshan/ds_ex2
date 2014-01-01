package org.tianshan.ds;

public class Message {
	public final static byte MSG_IN = 0;
	public final static byte MSG_OUT = 1;
	public final static byte MSG_NEWIN = 3;
	public final static byte MSG_NEWOUT = 4;
	public final static byte MSG_ASK = 5;
	
	private byte type;
	
	private int timestamp;
	
	private int port;
	
	private int inNum;
	
	private int outNum;
	
	public Message(String msg) {
		String[] attr = msg.split("@");
		
		if (attr.length < 3) {
			throw new RuntimeException("Error: msg num ");
		}
		
		this.type = Byte.parseByte(attr[0]);
		this.timestamp = Integer.parseInt(attr[1]);
		
		this.port = Integer.parseInt(attr[2]);
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
