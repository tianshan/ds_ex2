package org.tianshan.ds;

public class Way implements Runnable{
	private final static int STATE_RELEASED = 0;
	private final static int STATE_WANTED = 1;
	private final static int STATE_HELD = 2;
	
	private int state;
	
	public Way() {
		this.state = Way.STATE_RELEASED;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int s) {
		state = s;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	
}
