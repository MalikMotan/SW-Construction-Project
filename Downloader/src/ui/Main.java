package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import multipart.Multipart;
import multipart.Validator;
import sequence.FileSequenceReader;

public class Main extends JFrame {
	
	/** Logger used for testing and debugging purposes */
	final static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Main main = new Main();
				main.setVisible(true);
			}
		});
	}

	private JLabel imageView, progressLabel;
	private JTextArea textView;
	private JScrollPane scrollPane;
	private JTextField urlField;
	private JSlider animateSlider;
	private JLabel sliderLabel;
	private JButton stepButton;

	private Timer timer;
	private InputStream multipart;
	private String fileType;

	public static final String SEQ_SUFFIX = "-seq";
	public static final String MANIFEST_SUFFIX = ".segments";

	/**
	 * Creates a frame with controls for downloading and previewing multi-part
	 * files and controls for animating file sequence streams.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Multi-part Downloader");

		urlField = new JTextField(40);
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					startDownload();

				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		stepButton = new JButton("Step");
		stepButton.setEnabled(false);
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downloadNextFileFromSequence();
			}
		});
		sliderLabel = new JLabel("Animation Speed:");
		sliderLabel.setEnabled(false);
		animateSlider = new JSlider(0, 10, 0);
		// animateSlider.setMajorTickSpacing(10);
		// animateSlider.setMinorTickSpacing(1);
		// animateSlider.setPaintTicks(true);
		// animateSlider.setPaintLabels(true);
		animateSlider.setEnabled(false);
		animateSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				animate();
			}
		});

		imageView = new JLabel();
		textView = new JTextArea();
		textView.setEditable(false);

		progressLabel = new JLabel("Nothing downloaded yet.");

		scrollPane = new JScrollPane(textView);
		JPanel urlPanel = new JPanel(new BorderLayout());
		urlPanel.add(urlField, BorderLayout.CENTER);
		urlPanel.add(progressLabel, BorderLayout.SOUTH);
		urlPanel.add(new JLabel("Download URL:"), BorderLayout.NORTH);
		JPanel sliderPanel = new JPanel(new BorderLayout());
		sliderPanel.add(sliderLabel, BorderLayout.NORTH);
		sliderPanel.add(animateSlider, BorderLayout.SOUTH);
		JPanel controls = new JPanel();
		controls.add(startButton);
		controls.add(stepButton);
		controls.add(sliderPanel);
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(urlPanel, BorderLayout.CENTER);
		controlPanel.add(controls, BorderLayout.EAST);
		add(controlPanel, BorderLayout.SOUTH);
		add(scrollPane, BorderLayout.CENTER);

		setSize(600, 400);
	}

	/**
	 * Begins the download. Downloads and previews the entire stream if it's a
	 * normal file. If it is a sequence file (with suffix .seq), downloads and
	 * previews one file at a time.
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private void startDownload() throws MalformedURLException, IOException {
		finishDownload();

		String url = urlField.getText();

		boolean isSequence = false;
		try {
			// Check if the url is valid
			if(!Validator.isUrlExists(url)) {
				logger.trace("The url is invalid!");
				progressLabel.setText("The url you have entered is invalid!");
				return;
			}
			String path = new URL(url).getPath();

			if (path.endsWith(".cgi")) // ignore .cgi suffix
				path = path.substring(0, path.length() - ".cgi".length());

			if (path.endsWith(MANIFEST_SUFFIX)) { // ignore metafile suffix
													// .segments
				path = path.substring(0, path.length() - MANIFEST_SUFFIX.length());
				isSequence = true;
			}
			if (path.endsWith(SEQ_SUFFIX)) { // note, then remove sequence type
												// -seq
				path = path.substring(0, path.length() - SEQ_SUFFIX.length());
			}
			if(!path.substring(path.lastIndexOf('.') + 1).contains("part")) {
				// file type is everything after last '.':
				fileType = path.substring(path.lastIndexOf('.') + 1);
			} else {
				fileType = path.substring(path.lastIndexOf('.')-3, path.lastIndexOf('.'));
			}
		} catch (MalformedURLException e) {
			fileType = "";
		}
		try {
			multipart = Multipart.openStream(url);
		} catch(Exception ex) {
			progressLabel.setText("Downloading has encoutered a problem, failed!");
			logger.fatal("Download failed!");
		}
		if (!isSequence)
			downloadSingleFile();
		else {
			progressLabel.setText("Downloading sequence of files...");
			stepButton.setEnabled(true);
			animateSlider.setValue(0);
			animateSlider.setEnabled(true);
			sliderLabel.setEnabled(true);

			timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					downloadNextFileFromSequence();
				}
			});

		}
	}

	/**
	 * Adjusts the animation timer based on the animation slider. This includes
	 * starting or stopping the timer, if necessary.
	 */
	private void animate() {
		if (timer != null) {
			if (animateSlider.getValue() == 0)
				timer.stop();
			else {
				// slider value is fps, so delay by 1000/fps milliseconds:
				timer.setDelay(1000 / animateSlider.getValue());
				timer.start();
			}
		}
	}

	private void finishDownload() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		try {
			if (multipart != null)
				multipart.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		multipart = null;
		stepButton.setEnabled(false);
		animateSlider.setEnabled(false);
		sliderLabel.setEnabled(false);
	}

	private void downloadSingleFile() {
		try {
			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int read = 0;
			while (read != -1) {
				dest.write(buf, 0, read); // accumulate file into dest
				read = multipart.read(buf);
			}
			progressLabel.setText("Download succeeded.");
			preview(dest.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			progressLabel.setText("Download failed.");
		} finally {
			finishDownload();
		}
	}

	private void downloadNextFileFromSequence() {
		try {
			byte[] data = FileSequenceReader.readOneFile(multipart);
			if (data != null)
				preview(data);
			else { // no more sub-files
				progressLabel.setText("Sequence finished.");
				finishDownload();
			}
		} catch (IOException e) {
			e.printStackTrace();
			progressLabel.setText("Download failed.");
			finishDownload();
		}
	}

	private void preview(byte[] data) {

		if (fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("gif") || fileType.equalsIgnoreCase("png")) {
			imageView.setIcon(new ImageIcon(data));
			scrollPane.setViewportView(imageView);
		} else if (fileType.equalsIgnoreCase("txt")) {
			textView.setText(new String(data));
			scrollPane.setViewportView(textView);
		} else {
			textView.setText("[unknown file type]");
			scrollPane.setViewportView(textView);
		}
	}
}
