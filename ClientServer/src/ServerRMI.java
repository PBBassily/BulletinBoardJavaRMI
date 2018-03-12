import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
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
	private Semaphore rSeqLock = new Semaphore(1);
	private int rNum = 0;

	private static BufferedWriter readersFile, writersFile;

	protected ServerRMI() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	@Override
	public RequestState readVal(int rId) throws RemoteException {
		RequestState state = new RequestState();

		// Set received seq number
		try {
			rSeqLock.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		state.setrSeq(rSeq++);
		rNum++; // Increment number of readers
		rSeqLock.release();

		// sleep
		try {
			System.out.println(" >> Thread " + rId + " will sleep");
			Thread.sleep((long) (Math.random() * 1000 * 10));
			System.out.println(" >> Thread " + rId + " woke up");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// perform read operation, set severed seq number and write into log file
		try {
			valLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state.setVal(remoteObject);
		state.setsSeq(sSeq++);

		// write into readers' log file
		String logString = String.format("%-10s %-10s %-10s %-10s%n", state.getsSeq(), state.getVal(), rId, rNum);
		writeIntoFile(readersFile, logString);
		valLock.release();

		// decrement number of readers
		try {
			rSeqLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		rNum--;
		rSeqLock.release();

		return state;
	}

	/**
	 * 
	 */
	@Override
	public RequestState writeVal(int wId, int val) throws RemoteException {
		RequestState state = new RequestState();

		// Set received seq number
		try {
			rSeqLock.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		state.setrSeq(rSeq++);
		rSeqLock.release();

		// sleep
		try {
			System.out.println(" >> Thread " + wId + " will sleep");
			Thread.sleep((long) (Math.random() * 1000 * 10));
			System.out.println(" >> Thread " + wId + " woke up");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// perform write operation, set severed seq number and write into log file	
		try {
			valLock.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		remoteObject = val;
		state.setVal(val); 
		state.setsSeq( sSeq++); 

		// write into readers' log file
		String logString = String.format("%-10s %-10s %-10s%n", state.getsSeq(), state.getVal(), wId);
		writeIntoFile(writersFile, logString);

		valLock.release();

		return state;
	}

	/**
	 * Write into log file
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
	
	
	public static void main(String[] args) {
		try {

			String serverName = args[0] ;
			String serverIP = args[1];
			String rmiRegPort = args[2];
			
			
			// logs files
			File readersLogFile= new File("ServerLogs"+ File.separator +"Readers" );
			readersLogFile.getParentFile().mkdirs();
			readersFile = new BufferedWriter(new FileWriter(readersLogFile));
			readersFile.append("Readers\n");
			readersFile.append(String.format("%-10s %-10s %-10s %-10s%n",
					"sSeq", "oVal", "rId", "rNum"));

			File writersLogFile= new File("ServerLogs"+ File.separator +"Writers" );
			writersFile = new BufferedWriter(new FileWriter(writersLogFile));
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
