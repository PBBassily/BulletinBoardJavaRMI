import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIUtilInterface extends Remote{

   
	public RequestState readVal(int rId) throws RemoteException;
   
	public RequestState writeVal (int wId, int num)  throws RemoteException;
	
	
	
}