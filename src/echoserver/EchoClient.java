package echoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClient {
	public static final int PORT_NUMBER = 6013;
	public static final String SERVER_ADDRESS = "localhost";

	public static void main(String[] args) throws IOException {
		EchoClient client = new EchoClient();
		client.start();
	}

	private void start() throws IOException {
		Socket socket = new Socket(SERVER_ADDRESS, PORT_NUMBER);
		InputStream socketInputStream = socket.getInputStream();
		OutputStream socketOutputStream = socket.getOutputStream();

		// Create and start separate threads for reading from and writing to the socket
		Thread reader = new Thread(new SocketReader(socketInputStream));
		Thread writer = new Thread(new SocketWriter(socketOutputStream));

		reader.start();
		writer.start();

		try {
			// Wait for writer to finish (when user ends input)
			writer.join();
			// Signal end of output to server
			socket.shutdownOutput();
			// Wait for reader to finish processing server's response
			reader.join();
		} catch (InterruptedException e) {
			System.err.println("Thread interrupted: " + e);
		} finally {
			// Clean up resources
			socket.close();
		}
	}

	/**
	 * Handles reading data from the server socket and writing to stdout
	 */
	private static class SocketReader implements Runnable {
		private final InputStream in;

		public SocketReader(InputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			try {
				byte[] buffer = new byte[1024];
				int bytesRead;
				// Read from socket until end of stream
				while ((bytesRead = in.read(buffer)) != -1) {
					// Write received data to stdout
					System.out.write(buffer, 0, bytesRead);
					System.out.flush(); // Ensure output is displayed immediately
				}
			} catch (IOException e) {
				System.err.println("Error reading from server: " + e);
			}
		}
	}

	/**
	 * Handles reading data from stdin and writing to the server socket
	 */
	private static class SocketWriter implements Runnable {
		private final OutputStream out;

		public SocketWriter(OutputStream out) {
			this.out = out;
		}

		@Override
		public void run() {
			try {
				byte[] buffer = new byte[1024];
				int bytesRead;
				// Read from stdin until end of stream (Ctrl+D/Ctrl+Z)
				while ((bytesRead = System.in.read(buffer)) != -1) {
					// Write input data to socket
					out.write(buffer, 0, bytesRead);
					out.flush();
				}
			} catch (IOException e) {
				System.err.println("Error writing to server: " + e);
			}
		}
	}
}