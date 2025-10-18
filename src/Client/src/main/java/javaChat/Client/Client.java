package javaChat.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private String address;
	private int port;
	private String password;
	private Console console;
	private String username;

	public void close() {
		
	}
	
	public void start() {
		System.out.println("Client started");
		Socket sock;

		this.console = new Console(System.out, System.in, this);

		while (true) {

			System.out.print("Insert server ip: ");
			this.address = this.console.read();

			while (!this.address.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$")
					&& !this.address.equals("localhost")) {
				System.out.println("Invalid ip");
				System.out.print("Insert server ip: ");
				this.address = this.console.read();
			}

			String port = null;

			while (true) {
				try {
					System.out.print("Insert server port: ");
					port = this.console.read();
					if (Integer.parseInt(port) > 65535)
						throw new NumberFormatException();
					break;
				} catch (NumberFormatException e) {
					System.out.println("Invalid port");
				}
			}

			this.port = Integer.parseInt(port);

			this.console.print("Insert password: ");
			this.password = this.console.read();
			
			this.console.print("Insert username: ");
			this.username = this.console.read();

			try {
				sock = new Socket(this.address, this.port);

				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

				out.write(this.password);
				out.newLine();
				out.flush();

				String response = in.readLine();
				this.console.println(response);
				
				if(response.equals("Authorized")) {
					new Thread(new ClientServer(this.console, out, this.username)).start();
					new Thread(new ServerClient(this.console, in)).start();
					return;
				
				} else {
					sock.close();
				}
			} catch (UnknownHostException e) {
				System.out.println("Invalid address/port combination");
			} catch (IOException e) {
				System.out.println("Connection dropped");
			}
		}
	}

	class ClientServer implements Runnable {

		private Console console;
		private BufferedWriter out;
		private String username;
		
		private ClientServer(Console console, BufferedWriter out, String username) {
			this.console = console;
			this.out = out;
			this.username = username;
		}

		@Override
		public void run() {
			try {
				while (true) {
					
					this.console.printStart();
					this.out.write("[" + this.username + "] ");
					this.out.write(this.console.read());
					this.out.newLine();
					this.out.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class ServerClient implements Runnable {

		private BufferedReader in;
		private Console console;

		private ServerClient(Console console, BufferedReader in) {
			this.in = in;
			this.console = console;
		}

		@Override
		public void run() {
			try {
				while (true) {
					String tmp = in.readLine();
					this.console.println(tmp);
					this.console.printStart();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
