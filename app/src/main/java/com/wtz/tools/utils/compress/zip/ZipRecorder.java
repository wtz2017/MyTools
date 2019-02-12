package com.wtz.tools.utils.compress.zip;

public class ZipRecorder {

	private long totalLength;

	private long currentLength;

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public synchronized void addCurrentLength(long length) {
		currentLength += length;
	}

	public long getCurrentLength() {
		return currentLength;
	}
}