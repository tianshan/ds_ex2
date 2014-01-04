package org.tianshan.ds;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		
//		TCPTest();
		
		System.out.println("begin test...");
		System.out.println("Usage: inNum,outNum");
		runTest();
		
	}
	
	public static int carportNum = 3;
	
	public static Way[] ways;
	
	public static void runTest() {
		
		// init
		int[] ports = {10001, 10002, 10003, 10004, 10005};
		ways = new Way[ports.length];
		for (int i=0; i<ports.length; i++) {
			int type;
			if (i>=1) type = Way.TYPE_ENTRANCE;
			else type = Way.TYPE_EXIT;
			
			ways[i] = new Way(type, ports.length, carportNum, ports[i], false);
			ways[i].addWay(ports);
			
			Thread t = new Thread(ways[i]);
			t.start();
		}
		// end of init
		
		TCPSocket tcp = null;
		try {
			tcp = new TCPSocket(10000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scanner cin = new Scanner(System.in);
		while (true) {
			String cmd = cin.next();
			String[] attr = cmd.split(",");
			if (attr.length == 2) {
				int inNum = Integer.parseInt(attr[0]);
				int outNum = Integer.parseInt(attr[1]);
				
				Message msg;
				
				msg = new Message(Message.MSG_IN, 0, 0);
				for (int i=0; i<inNum; i++) {
					int index = (int)(Math.random()*(ports.length-1))+1;
					int port = ports[index];
					
					tcp.send(port, msg);
				}
				
				msg = new Message(Message.MSG_OUT, 0, 0);
				for (int i=0; i<outNum; i++)
					tcp.send(ports[0], msg);
				
			}else {
				break;
			}
				
		}
	}
	
	public static void printState() {
		// check state
		int in=0, out=0;
		for (int i=0; i<ways.length; i++) {
			in += ways[i].getInNum();
			out += ways[i].getOutNum();
		}
		System.out.println("--state--");
		System.out.println("total:"+carportNum);
		System.out.println("in  num:"+in);
		System.out.println("out num:"+out);
		
		System.out.println("--timestamp--");
		for (int i=0; i<ways.length; i++) {
			System.out.println(ways[i].getPort()+":"+ways[i].getTimestamp());
		}
	}
	
	public static void TCPTest() {
		TCPTestThread[] threads = new TCPTestThread[2];
		
		threads[0] = new TCPTestThread(12345, 0);
		threads[1] = new TCPTestThread(12346, 1);
		
		for (TCPTestThread t : threads) {
			t.start();
		}
		
	}
	
	public static class TCPTestThread extends Thread {
		
		TCPSocket tcp;
		
		int port;
		
		int type;
		
		public TCPTestThread(int port, int type) {
			this.port = port;
			this.type = type;
			try {
				tcp = new TCPSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			Message msg;
			switch(type) {
			case 0:
				msg = new Message("1@"+port+"@1");
				tcp.send("127.0.0.1", 12346, msg);
				break;
			case 1:
				msg = tcp.receive();
				break;
			}
		}
	}
	
}
