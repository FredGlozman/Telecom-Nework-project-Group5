package backend;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Singleton class that reads from and writes to a file hosted on a McGill server
 */
public class ServerTextFileIO {	
	private static final String SERVER_ROOT = "http://cs.mcgill.ca/";
	private static final String SERVER_USER = "fglozm";

	private static final String PHP_FILE = "write.php";
	private static final String FILE_NAME = "data.txt";
	
	private static final String BASE_URL = SERVER_ROOT + "~" + SERVER_USER + "/";
	private static final String URL = BASE_URL + FILE_NAME;
	private static final String PHP_URL = BASE_URL + PHP_FILE;
		
	private static ServerTextFileIO instance;

	/**
	 * Private constructor to ensure that users can't create 
	 * multiple instances of this class. 
	 */
	private ServerTextFileIO() {
		
	}
	
	/**
	 * @return the singleton instance of this class
	 */
	public static synchronized ServerTextFileIO getInstance() {
		if(instance == null) {
			instance = new ServerTextFileIO();
		}
		
		return instance;
	}
	
	/**
	 * @return the contents of the file
	 */
	public synchronized String read() {
		String contents = "";
		
		Scanner scanner = null;
		try {
			URL url = new URL(URL);
			
			scanner = new Scanner(url.openStream());
			if(scanner.hasNext()) {
				contents = scanner.useDelimiter("\\A").next();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(scanner != null) {
				try {
					scanner.close();
				} catch(Exception e) {
					//oh well...
				}
			}
		}

		return contents;
	}

	/**
	 * Overwrites the contents of the file
	 * @param data string to write to the file
	 */
	private synchronized void write(String data) {
		PrintStream ps = null;
		try {
			URL url = new URL(PHP_URL);
			URLConnection con = url.openConnection();

			con.setDoOutput(true);
			ps = new PrintStream(con.getOutputStream());
			ps.print(data);
		 
			con.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch(Exception e) {
					//oh well...
				}
			}
		}
	}
	
	/**
	 * Appends a string to the end of the file
	 * @param lineToAdd line to append to the file
	 */
	public synchronized void addLine(String lineToAdd) {
		lineToAdd = lineToAdd.trim();
		
		String fileContents = read().trim();
		if(fileContents == null || fileContents.equals("")) {
			fileContents = lineToAdd;
		} else {
			fileContents += "\n" + lineToAdd;
		}
		write(fileContents);
	}
	
	/**
	 * Removes all instances of a specified line in the file
	 * @param lineToRemove removes all lines in the file that match this line
	 */
	public synchronized void removeLine(String lineToRemove) {
		removeLines(new String[]{lineToRemove});
	}
	
	public void removeLines(String[] linesToRemove) {
		String fileContents = read();	
		if(fileContents == null || fileContents.equals("")) {
			return;
		}
		
		String[] lines = fileContents.split("\n");
		
		fileContents = "";
		for(String line : lines) {		
			if(line != null && !line.equals("") && !arrayContainsLine(linesToRemove, line)) {
				fileContents += line + "\n";
			}
		}
		fileContents.trim();
					
		write(fileContents);
	}
	
	private boolean arrayContainsLine(String[] array, String target) {
		boolean contains = false;
		for(String line : array) {
			if(line.equals(target)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
}