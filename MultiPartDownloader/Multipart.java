import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class Multipart {

	/** Logger used for debugging and testing purposes */
	private static Logger log = Logger.getLogger(Multipart.class);

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public InputStream openStream(URL url) throws IOException {

		log.info("Opening the manifest stream ...");
		// Don't continue if the url is not valid
		if (!StreamValidator.validate(url)) {
			return null;
		}

		log.info("The url is valid!");

		// Download the file if it is a direct file
		if (!isManifestStream(url)) {
			log.info("Downloading the file " + url.toString() + " directly ...");
			return downloadDirectFileFromUrl(url);
		} else { // download recursively
			log.info("Downloading the file " + url.toString() + " recursilvely ...");
		}
		return null;
	}

	// Read data from url
	private InputStream downloadDirectFileFromUrl(URL url) throws IOException {

		log.info("Downloading data from url: " + url.toString());
		return url.openStream();
	}

	// Check whether url has manifest stream (recursively)
	private boolean isManifestStream(URL url) {
		return false;
	}

}
