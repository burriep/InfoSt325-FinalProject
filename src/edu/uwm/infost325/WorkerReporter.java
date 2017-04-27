package edu.uwm.infost325;

public interface WorkerReporter {
	public void onCancelled();
	public void onComplete();
	public void onError(String message);
}
