package org.rhok.bribealert.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder {

	private static final String AUDIO_FILE = "-audio";
	final MediaRecorder recorder = new MediaRecorder();
	String path;
	boolean startedRecording = false;
	RecordingService rs;

	/**
	 * Creates a new audio recording at the given path (relative to root of SD
	 * card).
	 */
	public AudioRecorder(String path) {
		this.path = sanitizePath(path);
	}

	public void setParent(RecordingService arse) {
		rs = arse;
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
	public void start() throws IOException {

		path = sanitizePath("/recordings/" + System.currentTimeMillis()
				+ AUDIO_FILE + ".mp4");

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

		setPath(path);
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
		startedRecording = true;
	}

	public void setPath(String s) {
		try {
			File f = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/rpath.txt");
			f.delete();
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			osw.write(s);
			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops a recording that has been previously started.
	 */
	public void stop() throws IOException {
		if (startedRecording) {
			recorder.stop();
			recorder.release();
		}
	}
}
