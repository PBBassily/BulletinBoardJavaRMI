import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerListener extends Thread {

	private Socket socket;
	private int rSeq;
	private int sSeq;
	private Server serverInst;

	public ServerListener(Server server, Socket clientSocket, int rSeq) {
		this.socket = clientSocket;
		this.rSeq = rSeq;
		this.serverInst = server;
	}

	@Override
	public void run() {
		try {

			// Parse request
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String requestStr = reader.readLine();
			String[] req = requestStr.split("\\s", -1);

			System.out.println("=> In server thread " + req[1] + "  request : " + requestStr);

			// Writer :
			if (req[0].equalsIgnoreCase("Write")) {
				int wID = Integer.parseInt(req[1]);
				int newVal = Integer.parseInt(req[2]);

				// sleep
				System.out.println("=> In server thread " + req[1] + " will sleep");
				try {
					Thread.sleep((long) (Math.random() * 1000 * 10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("=> In server thread " + req[1] + " will wake up");

				// write new value
				sSeq = serverInst.writeVal(newVal, wID);

				// write the response into socket output file
				PrintWriter wtr = new PrintWriter(socket.getOutputStream(), true);
				wtr.println(String.format("%-10s %-10s%n", rSeq, sSeq));
			}
			// Reader
			else if (req[0].equalsIgnoreCase("Read")) {
				// Increase number of readers
				serverInst.incReadersNum();
				int rID = Integer.parseInt(req[1]);

				// sleep
				System.out.println("=> In server thread " + req[1] + " will sleep");
				try {
					Thread.sleep((long) (Math.random() * 1000 * 10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("=> In server thread " + req[1] + " will wake up");

				// read oVal and sSeq
				Server.status st = serverInst.readVal(rID);
				sSeq = st.getServSeq();
				int oVal = st.getVal();

				// write the response into socket output file
				PrintWriter wtr = new PrintWriter(socket.getOutputStream(), true);
				wtr.println(String.format("%-10s %-10s %-10s%n", rSeq, sSeq, oVal));

				// decrease number of readers
				serverInst.decReadersNum();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}