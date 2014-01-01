package org.tianshan.ds;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class TCPSocket {
	private static Socket localSocket;
//	private static int PORT = 12345;
	private PrintWriter out;
	private InputStream is;
	
	private InetAddress serverAddr;
	
	public TCPSocket(String str, int PORT) throws IOException{
		
		try{
			serverAddr = InetAddress.getByName(str);
			localSocket = new Socket(serverAddr, PORT);
			
//			out = new PrintWriter( 
//		    		new BufferedWriter( 
//		    				new OutputStreamWriter(clientsocket.getOutputStream())),true);
			is = localSocket.getInputStream();
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
	
	public boolean send(String toIp, int toPort, Message msg) {
		
		InetAddress toAddr;
		OutputStream os;
		
		try {
			toAddr = InetAddress.getByName(toIp);
			Socket toSocket = new Socket(toAddr, toPort);
			
			os = toSocket.getOutputStream();
			
			os.write(msg.toBytes());
			os.flush();
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public Message recive() {
		Message msg=null;
		try {

			byte[] buf = new byte[1024];
			int len = is.read(buf);
			
			String str = new String(buf, 0, len);
			msg = new Message(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public void closeSocket(){
		try{
			localSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Message sendForAskAndWait(int port) {
		// TODO:
		Message msg = null;
		
		
		
		return msg;
	}
	
	public boolean sendPermit(int port, int inNum, int outNum) {
		// TODO
		return true;
	}

}
