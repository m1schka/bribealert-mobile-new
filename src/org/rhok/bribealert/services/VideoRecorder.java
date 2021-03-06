package org.rhok.bribealert.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoRecorder extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = "VideoRecorder";
	public final MediaRecorder recorder = new MediaRecorder();
	SurfaceHolder holder;
	String path;
	Context c;

	/**
	 * Creates a new audio recording at the given path (relative to root of SD
	 * card).
	 */
	public VideoRecorder(Context con, AttributeSet attrs) {
		super(con, attrs);
		c = con;

		holder = getHolder();
		holder.addCallback(this);
	}

	public void setPath(String patha) {
		try {
			path = sanitizePath(patha);
			File f = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/rpath.txt");
			f.delete();
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(path);
			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String sanitizePath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += ".mp4";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ path;
	}

	/**
	 * Starts a new recording.
	 */
	public void start(Context c) throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted.  It is " + state
					+ ".");
		}

		// make sure the directory we plan to store the recording in exists
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created.");
		}

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		recorder.setVideoFrameRate(30);
		
		recorder.setOutputFile(path);
		Surface s = holder.getSurface();
		recorder.setPreviewDisplay(s);
		recorder.prepare();
		recorder.start();
	}

	/**
	 * Stops a recording that has been previously started.
	 */
	public void stop() throws IOException {
		try {
			recorder.stop();
			recorder.release();
		} catch (Exception e) {
		}
	}

	public String getPath() {
		return path;
	}

	public void surfaceChanged(SurfaceHolder sholder, int format, int width,
			int height) {
		Log.d(TAG, "Have todo something here");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "Have todo something here");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Have todo something here");
	}

}
