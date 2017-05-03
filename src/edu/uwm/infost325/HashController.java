package edu.uwm.infost325;

import java.io.*;
import java.security.MessageDigest;

import javax.swing.SwingWorker;

public class HashController extends SwingWorker<byte[], Integer> {
	private WorkerReporter reporter;
	private File sourceFile;
	private int sourceFileSize;
	boolean hadError;
	String errorMessage;
	File input;
	File output;

	public HashController(File source, WorkerReporter reporter) {
		this.sourceFile = source;
		this.reporter = reporter;
	}

	@Override
	protected byte[] doInBackground() throws Exception {
		sourceFileSize = (int) sourceFile.length();
		byte[] buffer = new byte[512];
		// create a new buffered file input stream for the source file
		try(InputStream iN = new BufferedInputStream(new FileInputStream(input))){
			try (OutputStream oS = new BufferedOutputStream(new FileOutputStream(output))){
				setProgress(0);

				// set the progress to 0
				int bytesRead = 0;
				int totalBytes;
				// get a MessageDigest instance
				MessageDigest a = MessageDigest.getInstance("SHA-256");
				// read the file in chunks and update the MessageDigest instance with every chunk read
				while((!isCancelled() && (bytesRead = iN.read(buffer)) > 0))
				{
					totalBytes += bytesRead;
					setProgress(totalBytes * 100 / sourceFileSize);
					byte[] hashReturn = a.update(buffer, 0, bytesRead);
					if (hashReturn != null ){
						oS.write(hashReturn);
					}
				}

			}
		}
	}

	@Override
	protected void done() {
		if (hadError) {
			reporter.onError(errorMessage);
		} else if (isCancelled()) {
			reporter.onCancelled();
		} else {
			reporter.onComplete();
		}
	}
}
