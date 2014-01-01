package org.tianshan.ds;

import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		
		test();
		
	}
	
	public static void test() {
		TCPTest[] threads = new TCPTest[2];
		
		threads[0] = new TCPTest(12345, 0);
		threads[1] = new TCPTest(12346, 1);
		
		for (TCPTest t : threads) {
			t.start();
		}
		
	}
	
	public static class TCPTest extends Thread {
		
		TCPSocket tcp;
		
		int port;
		
		int type;
		
		public TCPTest(int port, int type) {
			this.port = port;
			this.type = type;
			try {
				tcp = new TCPSocket("127.0.0.1", port);
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
				System.out.println("Port "+port+"send msg to 12346");
				break;
			case 1:
				msg = tcp.recive();
				System.out.println("Port "+port+"get msg from"+msg.getPort());
				break;
			}
		}
	}
	
}
