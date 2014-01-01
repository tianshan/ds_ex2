package org.tianshan.ds;

import java.io.IOException;
import java.util.ArrayList;

public class Entrance extends Way{

	private TCPSocket tcp;
	
	private ArrayList<Integer> allPorts;
	
	private int inNum;
	private int outNum;
	
	private int port;
	
	public Entrance(byte type, int entranceNum, int exitNum, int totalNum, int port) {
		super(type, entranceNum, exitNum, totalNum);
		
		this.port = port;
		
		try {
			tcp = new TCPSocket("127.0.0.1", port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		allPorts = new ArrayList<Integer>();
		
		inNum = 0;
	}

	private void setTimestamp(int time) {
		this.timestamp = time;
	}
	
	public void run() {
		while (true) {
			Message msg = tcp.recive();
			
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
		
		setState(STATE_WANTED);
		boolean result = waitIntoZone();
		
		if (result == false) {
			throw new RuntimeException("Port "+port+" Error:wait into zone");
		}
		
		setState(STATE_HELD);
		if (remainNum > 0) {
			inNum++;
			System.out.println("Port "+port+": get one car in");
		}else {
			System.out.println("Port "+port+": get one car in, but no carport remain");
		}
		
		
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
		remainNum = totalNum - inNum + outNum;
		for (int toPort:allPorts) {
			Message msg = tcp.sendForAskAndWait(toPort);
			if (msg != null) {
				nowNum++;
				remainNum += msg.getOutNum() - msg.getInNum();
			}
		}
		
		if (nowNum < allPorts.size()) return false;
		
		return true;
	}
	
	public void addEntrance(int port) {
		allPorts.add(port);
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
