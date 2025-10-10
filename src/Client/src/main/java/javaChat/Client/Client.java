package javaChat.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	
	private String address;
	private int port;
	private String password;
	
	public void start() {
		System.out.println("Client started");
		Socket sock;
		
		while(true) {
			
			this.gatherServerInfos();
			
			try {
				sock = new Socket(this.address, this.port);
				
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				
				out.write(this.password);
				out.newLine();
				out.flush();
				
				System.out.println(in.readLine());
				
				sock.close();
				
			} catch (UnknownHostException e) {
				System.out.println("Invalid address/port combination");
			} catch (IOException e) {
				System.out.println("Connection dropped");
			}
		}
	}
	
	private void gatherServerInfos() {
		Scanner s = new Scanner(System.in);
		
		System.out.print("Insert server ip: ");
    	this.address = s.nextLine();
    	
    	while(!this.address.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")
    			&& !this.address.equals("localhost")) {
    		System.out.println("Invalid ip");
    		System.out.print("Insert server ip: ");
    		this.address = s.nextLine();
    	}
    	
    	String port = null;
    	
    	while(true) {
    		try {
    			System.out.print("Insert server port: ");
            	port = s.nextLine();
    			if(Integer.parseInt(port) > 65535)
    				throw new NumberFormatException();
    			break;
    		} catch (NumberFormatException e) {
    			System.out.println("Invalid port");
    		}
    	}
		
    	this.port = Integer.parseInt(port);
    	
    	System.out.print("Insert password: ");
    	this.password = s.nextLine();
    	
    	s.close();
	}
}
