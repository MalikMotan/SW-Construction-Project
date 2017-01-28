package multipart;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * A class used for validating url
 * 
 * @author Malik, Ayman, Raed
 *
 */
public class Validator {

	/** Logger used for testing and debugging purposes */
	final static Logger logger = Logger.getLogger(Multipart.class);

	/**
	 * Validate a url
	 * 
	 * @param urlStr
	 * @return true if url is valid
	 */
	public static boolean validateURL(String urlStr) {
		try {
			logger.trace("Validating url: "+urlStr);
			URL url = new URL(urlStr); // Get a url object
			URLConnection urlConnection = url.openConnection(); // open a connection
			urlConnection.connect(); // connect
			logger.trace("url: "+urlStr+" is valid!");
			return true;
		} catch (MalformedURLException e) {
			logger.trace("url: "+urlStr+" is invalid!");
			return false;
		} catch (IOException e) {
			logger.trace("url: "+urlStr+" is invalid!");
			return false;
		}
	}

}
