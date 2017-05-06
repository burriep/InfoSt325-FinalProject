package edu.uwm.infost325;

import java.io.*;
import java.security.MessageDigest;

import javax.swing.SwingWorker;

public class HashController extends SwingWorker<byte[], Integer> {
	private WorkerReporter reporter;
	private File sourceFile;
	private int sourceFileSize;
	private boolean hadError;
	private String errorMessage;

	public HashController(File source, WorkerReporter reporter) {
		this.sourceFile = source;
		this.reporter = reporter;
	}

	@Override
	protected byte[] doInBackground() throws Exception {
		sourceFileSize = (int) sourceFile.length();
		byte[] digest = null;
		// create a new buffered file input stream for the source file
		try (InputStream iN = new BufferedInputStream(new FileInputStream(sourceFile))) {
			// set the progress to 0
			int bytesRead = 0;
			int totalBytes = 0;
			// get a MessageDigest instance
			MessageDigest a = MessageDigest.getInstance("SHA-256");
			// read the file in chunks and update the MessageDigest instance
			// with every chunk read
			byte[] buffer = new byte[512];
			while ((!isCancelled() && (bytesRead = iN.read(buffer)) > 0)) {
				totalBytes += bytesRead;
				setProgress(totalBytes * 100 / sourceFileSize);
				a.update(buffer, 0, bytesRead);
			}
			digest = a.digest();
		}
		return digest;
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
