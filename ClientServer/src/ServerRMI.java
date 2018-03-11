import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

public class ServerRMI  extends UnicastRemoteObject implements RMIUtilInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int remoteObject = -1;
	private int rSeq = 1;
	private int sSeq = 1;

	private Semaphore valLock = new Semaphore(1);
	private Semaphore rNumLock = new Semaphore(1);
	private Semaphore rSeqLock = new Semaphore(1);
	private int rNum = 0;

	private static BufferedWriter readersFile, writersFile;

	protected ServerRMI() throws RemoteException {

		super();

	}

	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;

	@Override
	public RequestState readVal(int rId) throws RemoteException {

		RequestState state = new RequestState();
		int tempRNum ;

		// acq
		try {
			rSeqLock.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		state.rSeq = rSeq++;

		rNum++;

		// release
		rSeqLock.release();

		// sleep
		try {
			System.out.println(" >> Thread " + rId + " will sleep");
			Thread.sleep((long) (Math.random() * 1000 * 10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(" >> Thread " + rId + " woke up");

		// acq
		try {
			valLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		state.val = remoteObject;

		state.sSeq = sSeq++;

		// release
		// write into readers' log file
		String logString = String.format("%-10s %-10s %-10s %-10s%n", state.sSeq,
				state.val, rId, rNum);
		writeIntoFile(readersFile, logString);
		
		valLock.release();

		try {
			rSeqLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rNum--;

		rSeqLock.release();
		return state;

	}

	/**
	 * 
	 * @param fileWriter
	 * @param st
	 */
	public void writeIntoFile(BufferedWriter fileWriter, String st) {
		try {
			fileWriter.append(st);
			fileWriter.flush();

		} catch (IOException e) {
			System.out.println(e + "=> In server , couldn't write into log file");
		}
	}

	@Override
	public RequestState writeVal(int wId, int val) throws RemoteException {
		RequestState state = new RequestState();

		// acq
		try {
			rSeqLock.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		state.rSeq = rSeq++;

		// release
		rSeqLock.release();

		// sleep
		try {
			System.out.println(" >> Thread " + wId + " will sleep");
			Thread.sleep((long) (Math.random() * 1000 * 10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(" >> Thread " + wId + " woke up");

		// acq
		try {
			valLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		remoteObject = val;
		state.val = val;

		state.sSeq = sSeq++;
		
		String logString = String.format("%-10s %-10s %-10s%n", state.sSeq,
				state.val, wId);
		writeIntoFile(writersFile, logString);

		// release
		valLock.release();

		return state;
	}

	public static void main(String[] args) {
		try {

			String serverName = args[0] ;
			String serverIP = args[1];
			String rmiRegPort = args[2];
			// logs files
			
			readersFile = new BufferedWriter(new FileWriter("Readers"));
			readersFile.append("Readers\n");
			readersFile.append(String.format("%-10s %-10s %-10s %-10s%n",
					"sSeq", "oVal", "rId", "rNum"));

			writersFile = new BufferedWriter(new FileWriter("Writers"));
			writersFile.append("Writers\n");
			writersFile.append(String.format("%-10s %-10s %-10s%n", "sSeq",
					"oVal", "wId"));

			System.setProperty("java.rmi.server.hostname",serverIP);  
			ServerRMI server = new ServerRMI();
			Naming.rebind("rmi://"+serverIP+":"+rmiRegPort+"/"+serverName, server); 	
			
			System.out.println("RMI Server is ready ");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

}
