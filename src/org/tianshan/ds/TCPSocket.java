package org.tianshan.ds;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;


public class TCPSocket {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private int port;
	private PrintWriter out;
	
	private boolean isDebug = false;
	
	public TCPSocket(int port) throws IOException{
		
		this.port = port;
		
		try{
			serverSocket = new ServerSocket(port);
		}catch(UnknownHostException e){
		}
	}
	
	public boolean send(String message){
		try {
		    out.println(message);
		    out.flush();
			// OutputStream out = clientsocket.getOutputStream();
			// out.write(message.getBytes());
		} catch(Exception e) {
		}
		return true;
	}
	
	public boolean send(int port, Message msg) {
		return send("127.0.0.1", port, msg);
	}
	
	public boolean send(String toIp, int toPort, Message msg) {
		
		InetAddress toAddr;
		OutputStream os;
		
		try {
			toAddr = InetAddress.getByName(toIp);
			clientSocket = new Socket(toAddr, toPort);
			
			os = clientSocket.getOutputStream();
			
			os.write(msg.toBytes());
			os.flush();
			
			if (isDebug)
				System.out.println("Port:"+port+"\tsend msg to\t"+toPort);
			
			clientSocket.close();
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public Message receive() {
		Message msg=null;
		try {
			clientSocket = serverSocket.accept();
			SocketAddress socketAddress = clientSocket.getRemoteSocketAddress();
			
			InputStream is = clientSocket.getInputStream();
			
			byte[] buf = new byte[1024];
			int len = is.read(buf);
			String str = new String(buf, 0, len);
			msg = new Message(str);
			
			if (isDebug)
				System.out.println("Port:"+port+"\tget msg from\t"+msg.getPort());
			
			clientSocket.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return msg;
	}
	
	public void closeSocket(){
		try{
			serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean sendAsk(int toPort, int timestamp) {
		Message msg = new Message(Message.MSG_ASK, port, timestamp);
		send(toPort, msg);
		return true;
	}
	
	public boolean sendPermit(int toPort, int inNum, int outNum) {
		Message msg = new Message(port, -1, inNum, outNum);
		send(toPort, msg);
		
		return true;
	}

}
