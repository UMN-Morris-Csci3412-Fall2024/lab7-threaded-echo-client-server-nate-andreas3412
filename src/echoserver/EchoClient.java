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

        Thread reader = new Thread(new SocketReader(socketInputStream));
        Thread writer = new Thread(new SocketWriter(socketOutputStream));

        reader.start();
        writer.start();

        try {
            writer.join();
            socket.shutdownOutput();
            reader.join();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e);
        } finally {
            socket.close();
        }
    }

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
				while ((bytesRead = in.read(buffer)) != -1) {
					System.out.write(buffer, 0, bytesRead);
					System.out.flush(); // Added missing flush
				}
			} catch (IOException e) {
				System.err.println("Error reading from server: " + e);
			}
		}
	}

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
				while ((bytesRead = System.in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					out.flush();
				}
			} catch (IOException e) {
				System.err.println("Error writing to server: " + e);
			}
		}
	}
}