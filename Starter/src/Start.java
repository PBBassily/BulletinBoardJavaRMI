

public class Start {
		
	// a remarkable delay between the start of the server and the clients 
	public final int SERVER_AFTER_RUN_DELAY = 2000; 
	
	public static void main (String[] args){
		
		Start invoker = new Start ();
		
		invoker.execute();	
	}
	
	/**
	 * loads the properties parser which returns properties object
	 * setups a server
	 * waits for two seconds
	 * starts N reader & M reader
	 * */
	private void execute() {

		PropertiesParser propertiesParser = new PropertiesParser();
		
		setupRMIRegistery(propertiesParser);
		setupServer(propertiesParser);
		
		try {
			
			Thread.sleep(SERVER_AFTER_RUN_DELAY);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i=0;
		for( ; i < propertiesParser.getReadersNum() ; i++){
			
			setupClient(propertiesParser , i,true);
			
		}
		for(; i < propertiesParser.getReadersNum()+ propertiesParser.getWritersNum() ; i++){
			
			setupClient(propertiesParser , i,false);
			
		}
		
	}

	/**
	 * 
	 * @param propertiesParser
	 * @param id
	 * @param isReader
	 * 
	 * parse client's ID, ip , user name , password , command and ssh on it
	 * runs on a brand new thread
	 */

	private void setupClient(PropertiesParser propertiesParser, int id , boolean isReader) {
		 
		String command = "cd Documents/gen_working_space/eclipse_java_ws/BulletinBoardJavaRMI/ClientServer/src;"
				+" java ClientRMI"
				+" "+ propertiesParser.getServerIP()
				+" "+ propertiesParser.getServerName()
				+" "+ isReader
				+" "+ id
				+" "+ propertiesParser.getAccessNum();
		
		int clientID = isReader ? id : (id-propertiesParser.getReadersNum());
		
		String clientIP = isReader ? propertiesParser.getReader(clientID):propertiesParser.getWriter(clientID) ;
		
		String clientUsername = isReader ? propertiesParser.getReaderUsername(clientID) : propertiesParser.getWriterUsername(clientID);
		
		String clientPassword = isReader ? propertiesParser.getReaderPassword(clientID) : propertiesParser.getWriterPassword(clientID);
		
		SSHChannelCreator sshChannelCreator = new SSHChannelCreator(clientUsername,clientPassword, clientIP  , command);
		
		sshChannelCreator.start();
		
		
		
		
	}
	
	/**
	 * 
	 * @param propertiesParser
	 * 
	 *  parse server's username , password , command , ip and ssh on it
	 *  runs on a brand new thread
	 */

	private void setupServer(PropertiesParser propertiesParser) {
		
		String command = "cd Documents/gen_working_space/eclipse_java_ws/BulletinBoardJavaRMI/ClientServer/src;"
		+"java ServerRMI "+propertiesParser.getServerName();
		
		SSHChannelCreator sshChannelCreator = new SSHChannelCreator(propertiesParser.getServerUsername(),
				propertiesParser.getServerPassword(), propertiesParser.getServerIP() , command);
		
		
		sshChannelCreator.start();
		
	}
	
	/**
	 * 
	 * @param propertiesParser
	 * 
	 *  parse server's username , password , command , ip and ssh on it
	 *  runs on a brand new thread
	 */

	private void setupRMIRegistery(PropertiesParser propertiesParser) {
		
		String command = "cd Documents/gen_working_space/eclipse_java_ws/BulletinBoardJavaRMI/ClientServer/src;"
//				+ "rmic ServerRMI ; "
				+ "rmiregistry ; ";
		
		SSHChannelCreator sshChannelCreator = new SSHChannelCreator(propertiesParser.getServerUsername(),
				propertiesParser.getServerPassword(), propertiesParser.getServerIP() , command);
		
		
		sshChannelCreator.start();
		
	}
	

}