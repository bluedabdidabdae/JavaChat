package javaChat.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private ServerSocket socket;
	
	// TODO: Save password through hash
	private String password = null;
	private Console console;
	
	public Server(int port) throws IOException {
		this.socket = new ServerSocket(port);
	}
	
	public boolean validateServerPassword(String toCheck) {
		return this.password.equals(toCheck);
	}
	
	public void close() {
		this.console.log("Closing server");
		System.exit(0);
	}
	
	public void start() {
		
		// creating console
		this.console = new Console(System.out, System.in, this);
		this.console.info("Server started");
		
		// setupping password
		do {
			this.console.print("Create server password: ");
			this.setPassword(this.console.read());
			
			this.console.print("Confirm server password: ");
			
		} while(!this.password.equals(this.console.read()));
		
		this.console.info("Password created");
		
		this.console.info("Server address: " + this.socket.getLocalSocketAddress());
		this.console.info("Server port: " + this.socket.getLocalPort());
		
		// starting interactive console
		// by separating it from main thread
		Thread t = new Thread(this.console);
		t.start();
		
		while(true) {
			try {
				this.console.log("Waiting for incoming connection...");
				Socket tmp = this.socket.accept();
			
				this.console.println("");
				this.console.log("Incoming connection from " + tmp.getRemoteSocketAddress());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(tmp.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(tmp.getOutputStream()));
				
				if(!this.validateServerPassword(in.readLine())) {
					this.console.log("Wrong password from client");
					this.console.log("Notifiying and closing connection...");
					out.write("Wrong password");
					out.newLine();
					out.flush();
					
					tmp.close();
				} else {
					this.console.log("Correct password from client");
					this.console.log("Notifiying");
					
					out.write("Authorized");
					out.newLine();
					out.flush();
					
					tmp.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
