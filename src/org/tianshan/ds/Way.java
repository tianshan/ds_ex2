package org.tianshan.ds;

import java.util.ArrayList;

public abstract class Way implements Runnable{
	public final static byte STATE_RELEASED = 0;
	public final static byte STATE_WANTED = 1;
	public final static byte STATE_HELD = 2;
	
	public byte state;
	
	public final static byte TYPE_EXIT = 0;
	public final static byte TYPE_ENTRANCE = 1;
	
	public byte type;
	
	public int entranceNum;
	public int exitNum;
	/** all carport num */
	public int totalNum;
	
	public int remainNum;
	
	public int timestamp;
	
	public ArrayList<Integer> waitQueue;
	
	
	public Way(byte type, int entranceNum, int exitNum, int totalNum) {
		this.state = Way.STATE_RELEASED;
		this.type = type;
		this.entranceNum = entranceNum;
		this.exitNum = exitNum;
		this.totalNum = totalNum;
		
		this.timestamp = 0;
		
		waitQueue = new ArrayList<Integer>();
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(byte s) {
		state = s;
	}

}
