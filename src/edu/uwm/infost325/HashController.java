package edu.uwm.infost325;

import java.io.*;

import javax.swing.SwingWorker;

public class HashController extends SwingWorker<byte[], Integer> {
	private WorkerReporter reporter;
	private File sourceFile;
	private int sourceFileSize;
	boolean hadError;
	String errorMessage;

	public HashController(File source, WorkerReporter reporter) {
		this.sourceFile = source;
		this.reporter = reporter;
	}

	@Override
	protected byte[] doInBackground() throws Exception {
		sourceFileSize = (int) sourceFile.length();
		byte[] digest = null;
		// TODO: implement method
		// create a new buffered file input stream for the source file
		// set the progress to 0
		// get a MessageDigest instance
		// read the file in chunks and update the MessageDigest instance with every chunk read
		// stop reading if this background task has been canceled
		// set the progress (total bytes read * 100 / sourceFileSize)
		// once finished reading the file, get the resulting digest from the MessageDigest instance and save in 'digest'
		// set the progress to 100%
		// if any problems arise, catch the error, record the error with 'hadError' and 'errorMessage'
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
