package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
public class Segment {
	
	/** A logger used for testing and debugging purposes */
	final Logger logger = Logger.getLogger(Segment.class);


	private String segmentURL = "";
	private String segmentContent = "";

	public Segment(String url) {
		segmentURL = url;
	}
	
	/**
	 * Given the urls of the segments, get their content
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public String getSegmentsContent() throws IOException, MalformedURLException {
		logger.trace("Reading the segments contents");

		URL segmentPath = new URL(segmentURL);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(segmentPath.openStream()));
		String inputLine;
		while ((inputLine = buffer.readLine()) != null) {
			segmentContent += inputLine;
		}
		logger.trace("Finished reading the segments contents");

		return segmentContent;
	}

}
