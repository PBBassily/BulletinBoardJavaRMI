import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;

public class ClientRMI {

	public static void main(String[] args) {
		try {
			String serverIP = args[0];
			String serverName = args[1];
			boolean isReader = (Boolean.parseBoolean(args[2])); // true: reader
																// , false:
																// writer
			int clientNum = Integer.parseInt(args[3]);
			int numberOfAccess = Integer.parseInt(args[4]);

			System.out.println("==> Client" + clientNum + "   started");

			// Open log file
			BufferedWriter fileWriter = null;
			try {
				fileWriter = new BufferedWriter(new FileWriter("log"
						+ clientNum));
				if (isReader) {
					fileWriter.append("Client type: Reader\n");
					fileWriter.append("Client Name: " + clientNum + "\n");
					fileWriter.append(String.format("%-10s %-10s %-10s%n",
							"rSeq", "sSeq", "oVal"));
				} else {
					fileWriter.append("Client type: Writer\n");
					fileWriter.append("Client Name: " + clientNum + "\n");
					fileWriter.append(String.format("%-10s %-10s%n", "rSeq",
							"sSeq"));
				}
			} catch (IOException e) {
				System.out.println(e + "==> Client" + clientNum
						+ "   couldn't open log file");
			}

			//
			String serverURL = "rmi://" + serverIP + "/"+serverName;
			RMIUtilInterface remoteServer = (RMIUtilInterface) Naming
					.lookup(serverURL);

			for (int i = 0; i < numberOfAccess; i++) {

				System.out.println("==> Client" + clientNum
						+ "   connected to server");
				String logSt = "";

				// Send request
				if (isReader) {
					RequestState state = remoteServer.readVal(clientNum);
					logSt = String.format("%-10s %-10s %-10s%n", state.rSeq,
							state.sSeq, state.val);
				}

				else {
					int newVal = clientNum;
					RequestState state = remoteServer.writeVal(clientNum,
							newVal);
					logSt = String.format("%-10s %-10s%n", state.rSeq,
							state.sSeq);

				}
				System.out.println(" ==> " + logSt);

				fileWriter.append(logSt + "\n");
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				System.out.println(e + "==> Client" + clientNum + "   couldn't close log file");
			}

		} catch (Exception e) {
			System.out.println("Exception:  " + e);
		}
		
	}

}
