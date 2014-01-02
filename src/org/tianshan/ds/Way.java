package org.tianshan.ds;

import java.io.IOException;
import java.util.ArrayList;

public class Way implements Runnable{
	private final static int STATE_RELEASED = 0;
	private final static int STATE_WANTED = 1;
	private final static int STATE_HELD = 2;
	
	private int state;
	
	public final static int TYPE_EXIT = 0;
	public final static int TYPE_ENTRANCE = 1;
	
	private int type;
	
	private int wayNum;
	/** all carport num */
	private int carportNum;
	
	private int remainNum;
	
	private int timestamp;
	
	private ArrayList<Integer> waitQueue;
	
	private TCPSocket tcp;
	
	private ArrayList<Integer> allPorts;
	
	private int inNum;
	private int outNum;
	
	private int port;
	
	/** wheather delay handle the message */
	private boolean delay;
	
	public Way(int type, int wayNum, int carportNum, int port, boolean delay) {
		this.state = Way.STATE_RELEASED;
		this.type = type;
		this.wayNum = wayNum;
		this.carportNum = carportNum;
		
		this.timestamp = 0;
		
		waitQueue = new ArrayList<Integer>();
		
		this.port = port;
		
		try {
			tcp = new TCPSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		allPorts = new ArrayList<Integer>();
		
		inNum = 0;
		outNum = 0;
		
		this.delay = delay;
		
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int s) {
		state = s;
	}
	
	private void setTimestamp(int time) {
		this.timestamp = time;
	}
	
	public void addWay(int port) {
		allPorts.add(port);
	}
	
	public void addWay(int[] ports) {
		for (int p : ports) {
			if (p == port) continue;
			addWay(p);
		}
	}
	
	public int getInNum() {
		return inNum;
	}
	public int getOutNum() {
		return outNum;
	}
	
	// message accept thread
	public void run() {
		while (true) {
			Message msg = tcp.recive();
			
			try {
				int milliesecond = (int)(Math.random()*1000);
				Thread.sleep(milliesecond);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			HandleMessgae thread = new HandleMessgae(msg);
			thread.start();
		}
	}
	
	// message handle thread
	public class HandleMessgae extends Thread {
		Message msg;
		
		public HandleMessgae(Message msg) {
			this.msg = msg;
		}
		
		public void run() {
			switch (msg.getType()) {
			case Message.MSG_IN:
				if (type != TYPE_ENTRANCE)
					throw new RuntimeException("Port "+port+" Error: get wrong car");
				getMsgIn(msg);
				break;
			case Message.MSG_OUT:
				if (type != TYPE_EXIT)
					throw new RuntimeException("Port "+port+" Error: get wrong car");
				getMsgOut(msg);
				break;
			case Message.MSG_NEWIN:
			case Message.MSG_NEWOUT:
				break;
			case Message.MSG_ASK:
				getMsgAsk(msg);
				break;
			}
		}
	}
	
	/**
	 * get one car
	 * @param msg
	 */
	private void getMsgIn(Message msg) {
		this.timestamp += 1;
		msg.setTimestamp(this.timestamp);
		
		System.out.println(port+" get car in, STATE_WANTED");
		
		setState(STATE_WANTED);
		boolean result = waitIntoZone();
		
		if (result == false) {
			throw new RuntimeException("Port "+port+" Error:wait into zone");
		}
		
		System.out.println(port+" STATE_HELD");
		
		setState(STATE_HELD);
		if (remainNum > 0) {
			inNum++;
			System.out.println("Port "+port+": get one car in");
		}else {
			System.out.println("Port "+port+": get one car in, but no carport remain");
		}
		
		System.out.println(port+" STATE_RELEASED");
		
		setState(STATE_RELEASED);
		replyWaitQueue();
		
	}
	
	/**
	 * ask permit to into critical zone
	 * wait until permit num is all entrance and exit num
	 * @return
	 */
	private boolean waitIntoZone() {
		int nowNum = 0;
		remainNum = carportNum - inNum + outNum;
		for (int toPort:allPorts) {
			if (toPort == this.port) continue;
			Message msg = tcp.sendForAskAndWait(toPort);
			if (msg != null) {
				nowNum++;
				remainNum += msg.getOutNum() - msg.getInNum();
			}
		}
		
		if (nowNum < allPorts.size()) return false;
		
		return true;
	}
	
	private void replyWaitQueue() {
		for (int toPort:waitQueue) {
			sendPermit(toPort);
		}
	}
	
	public void sendPermit(int toPort) {
		tcp.sendPermit(toPort, inNum, outNum);
	}
	
	/**
	 * get out car
	 * @param msg
	 */
	private void getMsgOut(Message msg) {
		System.out.println(port+" one car out");
		outNum++;
	}
	
	
	/**
	 * get ask for permit
	 * @param msg
	 */
	private void getMsgAsk(Message msg) {
		if (this.state == STATE_RELEASED) {
			setTimestamp(msg.getTimestamp());
			sendPermit(msg.getPort());
		}else if (this.state == STATE_HELD) {
			waitQueue.add(msg.getPort());
		}else if (this.state == STATE_WANTED) {
			// check timestamp
			if (msg.getTimestamp() > this.timestamp) {
				waitQueue.add(msg.getPort());
			}else if (msg.getTimestamp() == this.timestamp) {
				// check port num
				if (port < msg.getPort())
					waitQueue.add(msg.getPort());
				else
					sendPermit(msg.getPort());
			}else {
				sendPermit(msg.getPort());
			}
		}
	}

}
