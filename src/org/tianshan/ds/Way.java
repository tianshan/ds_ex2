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
	
	private int permitNum;
	
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
	
	private void setState(int s) {
		state = s;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	private void setTimestamp(int time) {
		this.timestamp = time;
	}
	
	public int getPort() { 
		return port;
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
			Message msg = tcp.receive();
			
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
				Main.printState();
				break;
			case Message.MSG_OUT:
				if (type != TYPE_EXIT)
					throw new RuntimeException("Port "+port+" Error: get wrong car");
				getMsgOut(msg);
				Main.printState();
				break;
			case Message.MSG_NEWIN:
			case Message.MSG_NEWOUT:
				break;
			case Message.MSG_ASK:
				getMsgAsk(msg);
				break;
			case Message.MSG_REPLAY:
				getMsgReplay(msg);
				break;
			default:
				throw new RuntimeException("Get wrong message");
			}
		}
	}
	
	/**
	 * get one car
	 * @param msg
	 */
	private void getMsgIn(Message msg) {
		// if already a car, sleep some while
		while (state != STATE_RELEASED) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.timestamp += 1;
		
		System.out.println(port+" get car in, STATE_WANTED");
		setState(STATE_WANTED);
		// get into zone
		sendAskAndWait();
		
		System.out.println(port+" STATE_HELD");
		setState(STATE_HELD);
		if (remainNum > 0) {
			inNum++;
			System.out.println("Port "+port+": one car get carport!");
		}else {
			System.out.println("Port "+port+": get one car in, but no carport remain");
		}
		
		System.out.println(port+" STATE_RELEASED");
		
		setState(STATE_RELEASED);
		replyWaitQueue();
		
	}
	
	private void sendAskAndWait() {
		permitNum = 0;
		
		remainNum = carportNum - inNum + outNum;
		for (int toPort:allPorts) {
			if (toPort == this.port) continue;
			tcp.sendAsk(toPort, timestamp);
		}
		
		while (permitNum<allPorts.size()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
	
	/**
	 * get replay message
	 * synchronized method
	 * @param msg
	 */
	private synchronized void getMsgReplay(Message msg) {
		if (state != STATE_WANTED) {
			throw new RuntimeException(port+" State is not STATE_WANTED, get wrong message from "+msg.getPort());
		}
		System.out.println(port+" get permit from "+msg.getPort());
		
		permitNum++;
		remainNum += msg.getOutNum() - msg.getInNum();
	}

}
