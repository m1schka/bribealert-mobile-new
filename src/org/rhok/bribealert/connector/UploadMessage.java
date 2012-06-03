package org.rhok.bribealert.connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.location.Location;
import android.util.Log;

public class UploadMessage implements MessageInterface {

	private static final String KEY_LONGITUDE = "lon";
	private static final String KEY_LATITUDE = "lat";
	private static final String KEY_DATE = "date";
	private static final String KEY_RECORD = "record";
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String TAG = "Notification";

	private MultipartEntity content;

	public UploadMessage(Location location, Date date, File record)
			throws FileNotFoundException {
		content = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

		try {
			addLocationData(location);
			addDate(date);
		} catch (UnsupportedEncodingException usee) {
			Log.d(TAG, "Unsorported encoding: " + usee.getMessage());
		}
		addRecord(record);
	}

	private void addLocationData(Location location)
			throws UnsupportedEncodingException {
		Double longitude = location.getLongitude();
		Double latitude = location.getLatitude();
		
		Log.d(TAG, "Got longitude: " + longitude + " and latitude: " + latitude);
		
		content.addPart(KEY_LONGITUDE, new StringBody(longitude.toString()));
		content.addPart(KEY_LATITUDE, new StringBody(latitude.toString()));
	}

	private void addDate(Date date) throws UnsupportedEncodingException {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
		
		Log.d(TAG, "Got date " + formatter.format(date));
		
		content.addPart(KEY_DATE, new StringBody(formatter.format(date)));
	}

	private void addRecord(File record) throws FileNotFoundException {
		if (record.exists()) {
			Log.d(TAG, "Got record " + record.getAbsolutePath());
			content.addPart(KEY_RECORD, new FileBody(record));
		}else{
			throw new FileNotFoundException("Couldn't find record "
					+ record.getAbsolutePath());
		}
	}

	public MultipartEntity getContent() {
		return content;
	}

	@Override
	public String getURI() {
		return content.toString();
	}
}