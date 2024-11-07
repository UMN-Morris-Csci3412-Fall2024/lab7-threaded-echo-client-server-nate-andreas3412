package echoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

	// REPLACE WITH PORT PROVIDED BY THE INSTRUCTOR
	public static final int PORT_NUMBER = 6013;
	private final ExecutorService threadPool;

	public EchoServer() {
		this.threadPool = Executors.newCachedThreadPool();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		EchoServer server = new EchoServer();
		server.start();
	}

	private void start() throws IOException, InterruptedException {
		try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
			while (true) {
				Socket socket = serverSocket.accept();

				// Put your code here.
				// This should do very little, essentially:
				// * Construct an instance of your runnable class
				// * Construct a Thread with your runnable
				// * Or use a thread pool
				// * Start that thread

				// Submit new client connection to thread pool for handling
				// This allows multiple clients to be handled concurrently
				threadPool.submit(new ClientHandler(socket));
			}
		}
	}

	/**
	 * Handles individual client connections in separate threads.
	 * Each instance processes one client's input/output stream.
	 */
	private static class ClientHandler implements Runnable {
		private final Socket socket;

		/**
		 * Creates new handler for client connection
		 * 
		 * @param socket The client socket to handle
		 */
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// Use try-with-resources to ensure streams are closed properly
			try (
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream()) {
				// Buffer for reading data in chunks
				byte[] buffer = new byte[1024];
				int bytesRead;

				// Read from client until end of stream (client disconnects)
				while ((bytesRead = in.read(buffer)) != -1) {
					// Echo data back to client
					out.write(buffer, 0, bytesRead);
					// Ensure data is sent immediately
					out.flush();
				}
			} catch (IOException e) {
				// Log any errors during client communication
				System.err.println("Error handling client: " + e);
			} finally {
				try {
					// Ensure socket is closed even if an error occurs
					socket.close();
				} catch (IOException e) {
					System.err.println("Error closing socket: " + e);
				}
			}
		}
	}
}