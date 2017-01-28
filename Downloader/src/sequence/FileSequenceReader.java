package sequence;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Static class for reading from a file-sequence stream.
 * 
 */
public class FileSequenceReader {

	final static Logger logger = Logger.getLogger(FileSequenceReader.class);

	/**
	 * Returns the data from the next sub-file in the given file sequence
	 * stream.
	 * <p>
	 * If no sub-files remain, returns null. If the stream ends prematurely,
	 * throws an EOFException.
	 */
	public static byte[] readOneFile(InputStream sequence) throws IOException, EOFException {
		// sequence files consist of a (4-byte) int giving the size of the
		// sub-file,
		// followed by the sub-file, followed by another size, followed by the
		// sub-file,
		// and so on until EOF

		logger.trace("In function: FileSequenceReader.readOneFile()");
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] file = new byte[4];
		if ((sequence.read(file)) != -1) {

			String s = "";
			for (byte b : file) {
				s += Integer.toBinaryString(b & 0xFF);
			}
			int size = Integer.parseInt(s, 2);
			logger.debug("The size of the file is:" + size);

			byte[] data = new byte[size];
			int noOfReadBytes = sequence.read(data, 0, data.length);
			buffer.write(data, 0, noOfReadBytes);
		}
		buffer.flush();
		logger.trace("Finished function: FileSequenceReader.readOneFile()");
		return buffer.toByteArray();

	}

}
