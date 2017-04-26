package edu.uwm.infost325;

import java.util.Observable;

public abstract class AsyncController extends Observable {
	public abstract void cancel();
	public abstract boolean isCanceled();
	public abstract void setMaxProgress(long max);
	public abstract void setProgress(long progress);
	public abstract double getProgress();
	public abstract void setDone();
	public abstract boolean isDone();
}
