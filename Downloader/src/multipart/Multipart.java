package multipart;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Static class for reading from a multipart stream.
 *
 * @author Malik, Ayman, Raed
 */
public class Multipart {

	/** Logger used for testing and debugging purposes */
	final static Logger logger = Logger.getLogger(Multipart.class);

	/**
	 * Returns an InputStream which streams from the given url.
	 * <p>
	 * If the url ends with the suffix .segments or has the MIME type
	 * text/segments-manifest it is treated as a multipart manifest stream, and
	 * the returned InputStream streams the data represented by the manifest,
	 * not the contents of the manifest itself.
	 * <p>
	 * Otherwise, when the url does not point to a manifest, the returned input
	 * stream streams data directly from the url target.
	 */
	public static InputStream openStream(String url) throws IOException {
		InputStream inputStream = null;
		if (Validator.validateURL(url)) {
			logger.trace("The url " + url + " is valid!");
			logger.trace("Openning stream for URL: " + url);
			inputStream = new URL(url).openStream();
		} else {
			logger.trace("The url " + url + " is invalid!");
		}
		return inputStream;
	}

}
