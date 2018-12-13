package com.wtz.tools.utils.zip;

class ZipRecorder {

	private long totalLength;

	private long currentLength;

	long getTotalLength() {
		return totalLength;
	}

	void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	synchronized void addCurrentLength(long length) {
		currentLength += length;
	}

	long getCurrentLength() {
		return currentLength;
	}
}