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

	private static final String PHP_WRITE_FILE = "write.php";
	   private static final String PHP_DELETE_FILE = "delete.php";

	private static final String BASE_URL = SERVER_ROOT + "~" + SERVER_USER + "/";
	
	private static final String PHP_WRITE_URL = BASE_URL + PHP_WRITE_FILE;
	  private static final String PHP_DELETE_URL = BASE_URL + PHP_DELETE_FILE;

	private static ServerTextFileIO instance;

	/**
	 * Private constructor to ensure that users can't create 
	 * multiple instances of this class. 
	 */
	private ServerTextFileIO() {
		
	}
	
	private static void phpRequest(String URL, String input) {
        PrintStream ps = null;
        try {
            URL url = new URL(URL);
            URLConnection con = url.openConnection();

            con.setDoOutput(true);
            ps = new PrintStream(con.getOutputStream());
            ps.print(input);
         
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
	 * @return the singleton instance of this class
	 */
	public static synchronized ServerTextFileIO getInstance() {
		if(instance == null) {
			instance = new ServerTextFileIO();
		}
		
		return instance;
	}
	
	public synchronized void delete(String fileName) {
	    //protect for accidentally deleting the player pool
	    if(fileName.equals(PlayerPool.PLAYER_POOL_FILE_NAME)) {
	        throw new RuntimeException("Error, you are not allowed to delete the player pool.");
	    }
	    
	    phpRequest(PHP_DELETE_URL, fileName);
	}
	
	/**
	 * @return the contents of the file
	 */
	public synchronized String read(String fileName) {
		String contents = "";
		
		Scanner scanner = null;
		try {
		    String URL = BASE_URL + fileName; 
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
	private synchronized void write(String fileName, String data) {
	    phpRequest(PHP_WRITE_URL, fileName + ":" +data);
	}
	
	/**
	 * Appends a string to the end of the file
	 * @param lineToAdd line to append to the file
	 */
	public synchronized void addLine(String fileName, String lineToAdd) {
		lineToAdd = lineToAdd.trim();
		
		String fileContents = read(fileName).trim();
		if(fileContents == null || fileContents.equals("")) {
			fileContents = lineToAdd;
		} else {
			fileContents += "\n" + lineToAdd;
		}
		write(fileName, fileContents);
	}
	
	/**
	 * Removes all instances of a specified line in the file
	 * @param lineToRemove removes all lines in the file that match this line
	 */
	public synchronized void removeLine(String fileName, String lineToRemove) {
		removeLines(fileName, new String[]{lineToRemove});
	}
	
	public void removeLines(String fileName, String[] linesToRemove) {
		String fileContents = read(fileName);	
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
					
		write(fileName, fileContents);
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