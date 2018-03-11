import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * this class aims to parse the system properties from system.properties file
 *
 */
public class PropertiesParser {

	private InputStream inputStream = null;
	private Properties properties = null;

	public PropertiesParser() {

		properties = new Properties();

		try {
			inputStream = new FileInputStream("system.properties");
			properties.load(inputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getServerIP() {
		return properties.getProperty("RW.server");

	}

	public String getServerPort() {
		return properties.getProperty("RW.server.port");

	}

	public String getServerDirPath() {
		return properties.getProperty("RW.server.dir.path");

	}

	public String getServerName() {
		return properties.getProperty("RW.serverRMI.name");

	}

	public String getServerUsername() {
		return properties.getProperty("RW.server.username");

	}

	public String getServerPassword() {
		return properties.getProperty("RW.server.password");

	}
	public String getRMIRegisteryPort() {
		return properties.getProperty("RW.rmiregistry.port");

	}


	public int getReadersNum() {
		String readersNum = properties.getProperty("RW.numberOfReaders");
		return Integer.parseInt(readersNum);

	}

	public String getReader(int i) {
		return properties.getProperty("RW.reader" + i);

	}

	public String getReaderUsername(int i) {
		return properties.getProperty("RW.reader" + i + ".username");

	}

	public String getReaderPassword(int i) {
		return properties.getProperty("RW.reader" + i + ".password");

	}

	public int getWritersNum() {
		String writersNum = properties.getProperty("RW.numberOfWriters");

		return Integer.parseInt(writersNum);

	}

	public String getWriter(int i) {
		return properties.getProperty("RW.writer" + i);

	}

	public String getWriterUsername(int i) {
		return properties.getProperty("RW.writer" + i + ".username");

	}

	public String getWriterPassword(int i) {
		return properties.getProperty("RW.writer" + i + ".password");

	}

	public String getClientDirPath(int i, boolean isReader) {

		return isReader ? properties.getProperty("RW.reader" + i + ".dir.path")
				: properties.getProperty("RW.writer" + i + ".dir.path");

	}

	public int getAccessNum() {
		String accessNum = properties.getProperty("RW.numberOfAccesses");
		return Integer.parseInt(accessNum);

	}

	public static void main(String[] args) {

		// unit testing for the parser

		PropertiesParser parseTester = new PropertiesParser();
		System.out.println(parseTester.getServerIP());
		System.out.println(parseTester.getServerPort());
		System.out.println(parseTester.getReadersNum());
		System.out.println(parseTester.getReader(3));
		System.out.println(parseTester.getWritersNum());
		System.out.println(parseTester.getWriter(26));
		System.out.println(parseTester.getAccessNum());
		System.out.println(parseTester.getReaderPassword(0));
		System.out.println(parseTester.getReaderUsername(3));
		System.out.println(parseTester.getWriterPassword(1));
		System.out.println(parseTester.getWriterUsername(2));
		System.out.println(parseTester.getServerPassword());
		System.out.println(parseTester.getServerUsername());

	}

}
