package javaChat.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Server {

	private ServerSocket serverSocket;
	private Console console;
	private int maxHosts;
	private AtomicReferenceArray<ChatUser> userList;
	private int userCount = 0;
	
	// TODO: Save password through hash
	private String password = null;

	public Server(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	public void popUser(ChatUser toPop) {
		boolean sanityCheck = false;
		try {
			for (int i = 0; i < this.userList.length(); i++) {
				if(this.userList.get(i).equals(toPop)) {
					this.userList.set(i, null);
					this.userCount--;
					sanityCheck = true;
				}
			}
			
			if(!sanityCheck) {
				this.console.log("Chat user not found in users list, stopping server since in unsafe state");
				this.close();
			}
		} catch(NullPointerException e) {
			this.console.log("Forcefully removed user from users list");
			this.userCount--;
		}
	}
	
	public boolean validateServerPassword(String toCheck) {
		return this.password.equals(toCheck);
	}

	public void close() {
		this.console.log("Closing server");
		for (int i = 0; i < this.userList.length(); i++) {
			if(null != this.userList.get(i))
				this.userList.get(i).close();
		}
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

		} while (!this.password.equals(this.console.read()));

		this.console.info("Password created");

		do {
			this.console.print("Insert max hosts number: ");

			try {
				this.maxHosts = Integer.parseInt(this.console.read());
			} catch (NumberFormatException e) {
				this.console.eraseStart();
				this.console.println("Invalid number");
			}

		} while (0 >= this.maxHosts);

		this.userList = new AtomicReferenceArray<ChatUser>(this.maxHosts);
		
		this.console.info("Server address: " + this.serverSocket.getLocalSocketAddress());
		this.console.info("Server port: " + this.serverSocket.getLocalPort());

		// starting interactive console
		// by separating it from main thread
		Thread consoleThread = new Thread(this.console);
		consoleThread.start();

		while (true) {
			try {
				this.console.log("Waiting for incoming connection...");
				Socket tmpSocket = this.serverSocket.accept();

				this.console.log("Incoming connection");
				this.console.log("Address: " + tmpSocket.getRemoteSocketAddress());

				BufferedReader in = new BufferedReader(new InputStreamReader(tmpSocket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(tmpSocket.getOutputStream()));

				if(this.userCount >= this.maxHosts) {
					in.readLine();
					this.console.log("Max host number reached");
					this.console.log("Notifiying");
					
					out.write("Server is full");
					out.newLine();
					out.flush();
					
					tmpSocket.close();
				}
				else if (this.validateServerPassword(in.readLine())) {
					this.console.log("Correct password from client");
					this.console.log("Notifiying");

					out.write("Authorized");
					out.newLine();
					out.flush();

					ChatUser tmpUser = new ChatUser(in, out, tmpSocket, this.console, this.userList, this);
					this.userList.set(this.userCount++, tmpUser);
					Thread t = new Thread(tmpUser);
					t.start();
					
				} else {
					this.console.log("Wrong password from client");
					this.console.log("Notifiying and closing connection...");
					out.write("Wrong password");
					out.newLine();
					out.flush();

					tmpSocket.close();
				}

			} catch (IOException e) {
				this.console.log("Server crashed ):");
				this.close();
			}
		}
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
