// Uploader Thread implementation
package edu.ucla.cs.m117.grad;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.SharedPreferences;

public class Uploader extends Thread
{
	private int		iBytesRead = 0;
	private int 	iBytesOpen = 0;
	private int 	iBufferSize = 0;
	private int 	iMaxBufferSize = 1024;
	private byte[] 	buffer;
	private	boolean bUploadSuccess = false;
	private	String 	strUploadURL = "http://mactm.web44.net/mactm/upload.php";
	private String 	strLineEnd = "\r\n";
	private String 	strTwoHyphens = "--";
	private String 	strBoundary = "*****";
	private String	strFileName = "/sdcard/MACTM/hourly_data.csv";
	private FileInputStream 	fInputStream 		= null;
	private File				file				= null;
	private SharedPreferences	sharedPreference	= null;
	
	// private variables related to file upload
	private	URL					url 				= null;
	private HttpURLConnection 	httpConnection 		= null;
	private	DataOutputStream	dataOutputStream 	= null;
	private	InputStream			inputStream 		= null;
	
	// default constructor for upload class
	public Uploader()
	{
		iBytesRead 		= 0;
		iBytesOpen 		= 0;
		iBufferSize 	= 0;
		iMaxBufferSize 	= 1024;
		bUploadSuccess 	= false;
		strUploadURL 	= "http://mactm_server/upload.php";
		strLineEnd 		= "\r\n";
		strTwoHyphens 	= "--";
		strBoundary		= "*****";
		strFileName 	= "/sdcard/MACTM/hourly_data.csv";
		return;
	}
	
	// default constructor for upload class
	public Uploader(SharedPreferences sharedPreferenceIn)
	{
		iBytesRead 		= 0;
		iBytesOpen 		= 0;
		iBufferSize 	= 0;
		iMaxBufferSize 	= 1024;
		bUploadSuccess 	= false;
		strUploadURL 	= "http://mactm.web44.net/mactm/upload.php";
		strLineEnd 		= "\r\n";
		strTwoHyphens 	= "--";
		strBoundary		= "*****";
		strFileName 	= "/sdcard/MACTM/hourly_data.csv";
		this.sharedPreference	= sharedPreferenceIn;
		return;
	}
	
	// actual run function for the thread
	public void run()
	{
		try
		{
			file 			= new File(strFileName);			// create the file 
			fInputStream 	= new FileInputStream(file);		// open the input stream
			url				= new URL(strUploadURL);
			httpConnection  = (HttpURLConnection) url.openConnection();
			
			httpConnection.setDoInput(true);					// set http connection properties
			httpConnection.setDoOutput(true);
			httpConnection.setUseCaches(false);
		
			httpConnection.setRequestMethod("POST");			// set request properties
			httpConnection.setRequestProperty("Connection", "Keep-Alive");
			httpConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + strBoundary);
			
			// get handles to input and output streams of the connection
			dataOutputStream 	= new DataOutputStream(httpConnection.getOutputStream());
			
			dataOutputStream.writeBytes(strTwoHyphens + strBoundary + strLineEnd);
			dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + strFileName + "\"" + strLineEnd);
			dataOutputStream.writeBytes(strLineEnd);

			iBytesOpen 	= fInputStream.available();					// see how much of the buffer is available
			iBufferSize = Math.min(iBytesOpen, iMaxBufferSize);		// calculate buffer size
			buffer 		= new byte[iBufferSize];					// create buffer

			iBytesRead = fInputStream.read(buffer, 0, iBufferSize);	// read from the input file (csv)
			
			while (iBytesRead > 0) 									// read and write to connection while 
			{														// data still remains
				dataOutputStream.write(buffer, 0, iBufferSize);
				iBytesOpen 		= fInputStream.available();
				iBufferSize 	= Math.min(iBytesOpen, iMaxBufferSize);
				iBytesRead 		= fInputStream.read(buffer, 0, iBufferSize);
			}

			dataOutputStream.writeBytes(strLineEnd);				// write out end of transmission
			dataOutputStream.writeBytes(strTwoHyphens + strBoundary + strTwoHyphens + strLineEnd);

			fInputStream.close();									// close input stream
			dataOutputStream.flush();								// flush output stream to server
			
			// now check if the data was uploaded correctly
			inputStream = httpConnection.getInputStream();
			if (inputStream != null) 
			{
				int				iInt 		= 0;
				StringBuffer 	strBuffer 	= new StringBuffer();
				while (( iInt= inputStream.read()) != -1) 
				{
					strBuffer.append((char) iInt);
				}
				
				String s = strBuffer.toString();
				
				if (s.indexOf("UPLOAD_OK") != -1 || s.indexOf("UPLOADS_OK") != -1) 
				{
					bUploadSuccess = true;
				} 
				else 
				{
					bUploadSuccess = false;
				}
			}
			dataOutputStream.close();
		}
		catch (MalformedURLException urlException)
		{
			urlException.printStackTrace();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		
		// delete the file from the sd card if the upload was successful
		if(bUploadSuccess == true)
		{
			File	fileToDelete = new File("/sdcard/MACTM/hourly_data.csv");
			if(fileToDelete.exists())
			{
				fileToDelete.delete();
				sharedPreference.edit().putLong("LAST_FILE_UPLOAD_TIME", System.currentTimeMillis()).commit();
			}
		}
	}
};