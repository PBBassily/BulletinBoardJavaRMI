import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {

		String serverIP = args[0];
		int serverPortNum = Integer.parseInt(args[1]);
		boolean isReader = (Boolean.parseBoolean(args[2])); // true: reader , false: writer
		int clientNum = Integer.parseInt(args[3]);
		int numberOfAccess = Integer.parseInt(args[4]);

		System.out.println("==> Client" + clientNum + "   started");

		// Open log file
		BufferedWriter fileWriter = null;
		try {
			fileWriter = new BufferedWriter(new FileWriter("log" + clientNum));
			if (isReader) {
				fileWriter.append("Client type: Reader\n");
				fileWriter.append("Client Name: " + clientNum + "\n");
				fileWriter.append(String.format("%-10s %-10s %-10s%n", "rSeq", "sSeq", "oVal"));
			}
			else {
				fileWriter.append("Client type: Writer\n");
				fileWriter.append("Client Name: " + clientNum + "\n");
				fileWriter.append(String.format("%-10s %-10s%n", "rSeq", "sSeq"));
			}	
		} catch (IOException e) {
			System.out.println(e + "==> Client"+ clientNum +"   couldn't open log file");
		}

		// 
		for (int i = 0; i < numberOfAccess; i++) {
			Socket socket;
			try {
				socket = new Socket(serverIP, serverPortNum);
				System.out.println("==> Client" + clientNum + "   connected to server");

				// socket output file writer
				PrintWriter wtr = new PrintWriter(socket.getOutputStream(), true);
				// Send request
				if (isReader) {
					wtr.println("Read " + clientNum);
					System.out.println("==> Client" + clientNum + "   send : read");
				}
					
				else {
					int newVal = clientNum;
					wtr.println("Write " + clientNum + " " + newVal);
					System.out.println("==> Client" + clientNum + "   send : write "+ newVal);
				}
				
				// socket input file reader
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// Read Response
				String response = reader.readLine();
				fileWriter.append(response + "\n");
				System.out.println("==> Client" + clientNum + "   recieved : " + response);

			} catch (UnknownHostException e) {
				System.out.println(e + "==> Client" + clientNum + "   couldn't find server host");
			} catch (IOException e) {
				System.out.println(e + "==> Client" + clientNum + "   problem in accessing socket output file");

			}
		}
		try {
			fileWriter.close();
		} catch (IOException e) {
			System.out.println(e + "==> Client" + clientNum + "   couldn't close log file");
		}

	}
}