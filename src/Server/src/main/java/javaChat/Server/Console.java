package javaChat.Server;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Console implements Runnable {

	private PrintStream out;
	private Scanner in;
	private Server server;

	public Console(PrintStream out, InputStream in, Server server) {
		this.out = out;
		this.in = new Scanner(in);
		this.server = server;
	}

	@Override
	public void run() {
		String command;
		this.println("Welcome to server console!");
		do {
			this.print(" # ");
			command = this.read();
			this.parseCommand(command);
		} while (true);
	}

	public void parseCommand(String command) {
		switch (command) {

		case "whoami":
			this.println("root");
			break;

		case "change password":
			this.changeServerPassword();
			break;

		case "close server":
			this.closeServer();
			break;

		default:
			this.println("Unknown command");
		}
	}

	public boolean serverLogin() {

		int tries = 0;
		boolean logged = false;

		do {
			this.print("Insert current password: ");
			String tmp = this.read();

			logged = this.server.validateServerPassword(tmp);

			if (!logged) {
				this.println("Wrong password");
			}
		} while (2 > tries++ && !logged);

		if (!logged) {
			this.println("Operation failed");
		}

		return logged;
	}

	public void closeServer() {
		if (this.serverLogin()) {
			this.log("Attempting to close server");
			this.server.close();
			System.exit(0);
		}
	}

	public void changeServerPassword() {

		String tmp;

		if (this.serverLogin()) {
			do {
				this.print("Insert new password: ");
				tmp = this.read();

				this.print("Confirm new password: ");
			} while (!tmp.equals(this.read()));

			this.server.setPassword(tmp);

			this.log("New password set");
		}
	}

	public String read() {
		return this.in.nextLine();
	}

	public void print(String toPrint) {
		this.out.print(toPrint);
	}

	public void println(String toPrint) {
		this.out.println(toPrint);
	}

	public void log(String toPrint) {
		this.out.println("[LOG] " + toPrint);
	}

	public void info(String toPrint) {
		this.out.println("[INFO] " + toPrint);
	}
}
