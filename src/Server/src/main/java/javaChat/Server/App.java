package javaChat.Server;

import java.io.IOException;

public class App {
	public static void main(String[] args) {
		int port = 8081;
		Server s;

		try {
			System.out.println("Triying to start server");

			s = new Server(port);

			s.start();
		} catch (IOException e) {
			System.out.println("Server just detonated :(");
			System.out.println("I detonated because: " + e.getMessage());
			System.exit(1);
		}

		System.out.println("Server closed");
		System.exit(0);
	}
}
