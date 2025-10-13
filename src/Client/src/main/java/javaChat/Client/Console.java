package javaChat.Client;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Console {

	private PrintStream out;
	private Scanner in;
	@SuppressWarnings("unused")
	private Client client;

	public Console(PrintStream out, InputStream in, Client client) {
		this.out = out;
		this.in = new Scanner(in);
		this.client = client;
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
	
	public void printStart() {
		this.out.print("-> ");
	}
	
	public void eraseStart() {
		this.out.print("\b\b\b");
	}
}
