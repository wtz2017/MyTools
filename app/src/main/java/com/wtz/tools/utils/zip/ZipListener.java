package com.wtz.tools.utils.zip;

public interface ZipListener {

	public void onStart(String path);

	public void onEnd(String path);

	public void onError(String message);

	public void onProgress(long current, long total);
}