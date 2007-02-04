package com.thoughtworks.webdriver.firefox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

class ExtensionListener {
	private final Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private List responses = new LinkedList();
	
	public ExtensionListener(String extensionUrl, int port) {
		try {
			socket = new Socket(extensionUrl, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

	public String writeAndWaitForResponse(String command, String argument) {
		String output = command;
		if (argument != null)
			output += " " + argument;  // Yes, it's not terribly efficient is it?
//		System.out.println(output);
		out.println(output);
		
		String fullResponse =  waitForResponseFor(command);
		return fullResponse.substring(command.length() + 1);  // Strip out the command name and a space.
	}

	private String waitForResponseFor(String command) {
		try {
			return readLoop(command);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String readLoop(String command) throws IOException {
//		System.out.println("Waiting for response to: " + command);
		while (true) {
			String response = in.readLine();
//			System.out.println("Have seen: " + response);
			if (response.startsWith(command))
				return response;
			responses.add(response);
		}
	}
}
