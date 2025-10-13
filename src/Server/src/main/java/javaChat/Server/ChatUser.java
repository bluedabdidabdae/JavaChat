package javaChat.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ChatUser implements Runnable {

	private BufferedReader in;
	private BufferedWriter out;
	private Socket socket;
	private Console console;
	private AtomicReferenceArray<ChatUser> userList;
	private Server server;
	
	public ChatUser(BufferedReader in, BufferedWriter out, Socket socket, Console console, AtomicReferenceArray<ChatUser> userList, Server server) {
		this.in = in;
		this.out = out;
		this.socket = socket;
		this.console = console;
		this.userList = userList;
		this.server = server;
	}

	public void sendData(String data) throws IOException {
		this.out.write(data);
		this.out.newLine();
		this.out.flush();
	}

	public void close() {
		System.exit(0);
	}
	
	@Override
	public void run() {

		try {
			while (true) {

				String inputData = this.in.readLine();

				// telling all other user threads to send received data
				for (int i = 0; i < this.userList.length(); i++) {
					if(null != this.userList.get(i) && this != this.userList.get(i))
						this.userList.get(i).sendData(inputData);
				}

			}
		} catch (Exception e) {
			this.console.log("Connection with "+this.socket.getRemoteSocketAddress()+" dropped");
			this.console.log("Happened because of: "+e.getMessage());
			
			this.server.popUser(this);
		}
	}
}
