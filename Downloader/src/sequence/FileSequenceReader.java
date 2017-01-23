package sequence;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Static class for reading from a file-sequence stream.
 * 
 */
public class FileSequenceReader {
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
		int dataSize;
		try {
			dataSize = new DataInputStream(sequence).readInt();
			byte[] data = new byte[dataSize];
			int totalNumOfReadBytes = 0;
			while (totalNumOfReadBytes < dataSize) {
				// Read more data
				int numOfReadBytes = sequence.read(data, totalNumOfReadBytes, dataSize - totalNumOfReadBytes);
				// Check that the file has not ended
				if (numOfReadBytes == -1) // if no more data
					throw new EOFException("End of file, number of read bytes is " + totalNumOfReadBytes);
				totalNumOfReadBytes += numOfReadBytes;
			}
			return data;
		} catch (EOFException e) { // data ends here
			return null;
		}

	}
}
