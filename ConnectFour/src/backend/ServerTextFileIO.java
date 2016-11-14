package backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Singleton class that reads from and writes to a file hosted on a McGill server.
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
	private ServerTextFileIO() {}
	
	/**
	 * Send a PHP request to a specific script.
	 * @param urlString URL as a string indicating where the PHP file is stored.
	 * @param input Parameters to pass to the PHP script.
	 */
	private static void phpRequest(String urlString, String input) {
        PrintStream ps = null;
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();

            con.setDoOutput(true);
            ps = new PrintStream(con.getOutputStream());
            ps.print(input);
         
            con.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    //oh well...
                }
            }
        }
	}
	
	/**
	 * @return the singleton instance of this class
	 */
	public static synchronized ServerTextFileIO getInstance() {
		if (instance == null) {
			instance = new ServerTextFileIO();
		}
		
		return instance;
	}
	
	/**
	 * Delete a file from the server.
	 * @param fileName Name of the file to delete.
	 */
	public synchronized void delete(String fileName) {
	    //protect for accidentally deleting the player pool
	    if (fileName.equals(PlayerPool.PLAYER_POOL_FILE_NAME)) {
	        throw new RuntimeException("Error, you are not allowed to delete the player pool.");
	    }
	    
	    phpRequest(PHP_DELETE_URL, fileName);
	}
	
	/**
	 * Read the contents of a fine and return them.
	 * @return The contents of the file as a string.
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
	 * Overwrite the contents of the file.
	 * @param fileName Name of the file in which the overwrite is to be done.
	 * @param data string to write to the file.
	 */
	private synchronized void write(String fileName, String data) {
	    phpRequest(PHP_WRITE_URL, fileName + ":" + data);
	}
	
	/**
	 * Clear the contents of a file.
	 * @param fileName Name of the file to clear.
	 */
	public synchronized void clear(String fileName) {
	    phpRequest(PHP_WRITE_URL, fileName + ":" + "");
	}
	
	/**
	 * Create a file on the server.
	 * @param fileName Name of the file to be created.
	 */
	public synchronized void createFile(String fileName) {
		write(fileName, new String());
	}
	
	/**
	 * Append a string to the end of the file.
	 * @param lineToAdd line to append to the file.
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
	 * Remove all instances of a specified line in a file.
	 * @param fileName Name of a file from which the line is to be removed.
	 * @param lineToRemove Line to be matched.
	 */
	public synchronized void removeLine(String fileName, String lineToRemove) {
		removeLines(fileName, new String[]{lineToRemove});
	}
	
	/**
	 * Remove all instances of each line in a specified set of lines in a file.
	 * @param fileName Name of the file from which the lines are the be removed.
	 * @param linesToRemove Line to be matched.
	 */
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
	
	/**
	 * Verify whether a file exists on the server.
	 * @param fileName Name of the file to be verified.
	 * @return Boolean indicating whether the file exists.
	 */
	public boolean exists(String fileName) {
		try {
			read(fileName);
			return true;
		} catch (RuntimeException e) {
			if (e.getCause() instanceof FileNotFoundException)
				return false;
			throw e;
		}
	}
	
	/**
	 * Verify whether an array of strings contains a string.
	 * @param array Array of strings in question.
	 * @param target String to be matched.
	 * @return Boolean indicating whether array contains target.
	 */
	private static boolean arrayContainsLine(String[] array, String target) {
		for(String line : array)
			if(line.equals(target))
				return true;
		
		return false;
	}
}