// PedometerSensorService implementation
package edu.ucla.cs.m117.grad;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;

// The PEDOMETER Service Class 
public class PedometerSensorService extends Service 
{
	private 		SensorManager 			mySensorManager;		// SENSOR declarations
	private 		Sensor					sAccSensor;
	private 		Sensor					sMagSensor;
	private 		AccProcessingThread 	accProThread;			// PROCESSING THREAD declaration
	private 		PowerManager 			pmPowerManager;			// POWER MANAGER / CPU declaration
	private 		PowerManager.WakeLock 	pmwlWakeLock;
	private 		int						iAccelerometerSamplingRate = SensorManager.SENSOR_DELAY_FASTEST;
	private 		Handler 				handle;
	
	private  static AccelerometerBuffer		buffer = new AccelerometerBuffer();
	private  static long					iStepCount = 0;
	
	private boolean foregroundService 		= false;
	private static int ACC_SAMPLING_RATE 	= SensorManager.SENSOR_DELAY_FASTEST;	
	private final RemoteCallbackList<ICallBack> mcallbacks = new RemoteCallbackList<ICallBack>();
	
	// FUNCTION: OnCreate  	
	public void onCreate() 
	{
		super.onCreate();
			
		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);		// get system service handle
		sAccSensor		= mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	// get accelerometer sensor
		sMagSensor 		= mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	// get magnetic sensor
	
		mySensorManager.registerListener(mySensorListener, sAccSensor,	iAccelerometerSamplingRate);	// register listener
		mySensorManager.registerListener(mySensorListener, sMagSensor,	ACC_SAMPLING_RATE);				// register listener
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);	// intent for screen turning ON
		filter.addAction(Intent.ACTION_SCREEN_ON);							// add the necessary action
		registerReceiver(mReceiver, filter);								// register the receiver
						
		pmPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		pmwlWakeLock = pmPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorServiceCPU_Lock");	
		
		accProThread = new AccProcessingThread((Context)this);				// create the thread
		accProThread.setPriority(Thread.MAX_PRIORITY);						// set thread priority
		accProThread.start();												// start the thread
		
		// set the handler
		handle = mHandler;
	}
	
	// FUNCTION: onStartCommand
	public int onStartCommand(Intent intent, int flags, int startId) 
	{		
	    return START_STICKY;
	}

	// FUNCTION onBind
	public IBinder onBind(Intent arg0) 
	{
		return accBinder;
	}

	// SensorService Interface
	private final ISensorService2.Stub accBinder = new ISensorService2.Stub() 
	{
		// FUNCTION: registerCallback
		public void registerCallback(ICallBack cb) throws RemoteException 
		{
			if (cb != null)
				mcallbacks.register(cb);
		}

		// FUNCTION: unregisterCallback
		public void unregisterCallback(ICallBack cb) throws RemoteException 
		{
			if (cb != null)
				mcallbacks.unregister(cb);
		}

		// FUNCTION: startAcc
		public void startAcc() throws RemoteException 
		{		
			mySensorManager.registerListener(mySensorListener, sAccSensor,	ACC_SAMPLING_RATE);
			mySensorManager.registerListener(mySensorListener, sMagSensor,	ACC_SAMPLING_RATE);			
		}

		// FUNCTION: stopAcc
		public void stopAcc() throws RemoteException 
		{			
			mySensorManager.unregisterListener(mySensorListener);			
		}

		// FUNCTION: stopService
		public void stopService() throws RemoteException 
		{
			stopSelf();			
		}

		// FUNCTION: setSensitivity
		public void setSensitivity(int a) throws RemoteException 
		{			
		}
	};
			
	// FUNCTION: onDestroy
	public void onDestroy() {
			
		super.onDestroy();										// call base class destroy
		unregisterReceiver(mReceiver);							// un-register message receiver
		mySensorManager.unregisterListener(mySensorListener);	// un-register service listener
		accProThread = null;									// clear out the processing thread
		
		if(pmwlWakeLock.isHeld())
		{
			pmwlWakeLock.release();
		}
		
		if(foregroundService)
		{
			stopForeground(true);
		}
	}

	private final SensorEventListener mySensorListener = new SensorEventListener() 
	{
		private int sensorType;	
		
		// FUNCTION: onSensorChanged
		public void onSensorChanged(SensorEvent event) 
		{
			sensorType = event.sensor.getType();
			switch (sensorType)
			{
				case Sensor.TYPE_MAGNETIC_FIELD:		break;
				case Sensor.TYPE_ACCELEROMETER:	
					if(!buffer.isFull())
					{
						buffer.set(new RawValues(event.values.clone()));
					}
					break;				
			}
		}

		// FUNCTION: onAccuracyChanged
		public void onAccuracyChanged(Sensor sensor, int accuracy) 
		{}
	};
	
	private class AccProcessingThread extends Thread {		
		private final float 	CPU_WAKEUP_RATIO = 0.8f;	// The ration to the sampling window that the CPU need to be waken up
		private long		 	lastUpdate = 0;				// The time of previous generated Km
		private long 			curTime = 0;				// The time of the previous onSensorChanged() call
		
		// variables to hold acceleration values
		private float			fAccX 	= 0.f;
		private float			fAccY 	= 0.f;
		private float			fAccZ 	= 0.f;
		private float			fAccX_2 = 0.f;
		private float			fAccY_2 = 0.f;
		private float			fAccZ_2 = 0.f;
		private float			fMag	= 0.f;
		
		private float[]			fMagnitudeBuffer;
		private float[]			fMovingAverageBuffer;
		private int				iMagnitudeBufferSize 		= 4;
		private int				iMovingAverageBufferSize 	= 5;
		private int				iMagBufferIndex 			= 0;
		private int				iMovingAverageBufferIndex	= 0;
		private int				iAdjustedStepCount			= 0;
		private float			fAdjustmentFactor			= 0.f;
		
		private Context				contextApplication 	= null;
		private SharedPreferences 	sharedPreference 	= null;
		private String				strPhoneNumber		= null;
		private TelephonyManager	telephonyManager 	= null;
		
		// FUNCTION: AccProcessingThread
		public AccProcessingThread(Context c)
		{
			lastUpdate 				= System.currentTimeMillis();
			curTime 				= lastUpdate;
			fMagnitudeBuffer 		= new float[iMagnitudeBufferSize];			// create the buffers
			fMovingAverageBuffer 	= new float[iMovingAverageBufferSize];		
			iMagBufferIndex			= 0;										// reset the circular pointers
			iMovingAverageBufferIndex = 0;
			contextApplication		= getApplicationContext();
			sharedPreference		= PreferenceManager.getDefaultSharedPreferences(contextApplication);
			telephonyManager		= (TelephonyManager) contextApplication.getSystemService(TELEPHONY_SERVICE);
			strPhoneNumber			= telephonyManager.getLine1Number();
		}
		
		// FUNCTION: run
		public void run()
		{
			boolean p = false;
			while(true)
			{
				if(!buffer.isEmpty())
				{
					p = process(buffer.get());
					
					// Release CPU lock after writing the file
					if(pmwlWakeLock.isHeld() && p) 
					{						
						pmwlWakeLock.release();
					}					
				}
				else 
				{	
					// Wake up CPU if the sensor hasn't updated for half of the time window and the curentKm > 1
					if(System.currentTimeMillis()-curTime > CPU_WAKEUP_RATIO*1*1000 && !pmwlWakeLock.isHeld())
					{
						pmwlWakeLock.acquire();											
					}
					else if(!pmwlWakeLock.isHeld())
					{
						try 
						{
							Thread.sleep(200);
						} 
						catch (InterruptedException e) 
						{						
							e.printStackTrace();
						}
					}
					else
					{
						Thread.yield();
					}
				}				
			}
		}
			
		// FUNCTION: process
		private boolean process(RawValues r)
		{
			int			iIndex 				= 0;
			float		fMovingAverageSum 	= 0.f;
			float		fMovingAverage 		= 0.f;
			float		fThreshold 			= 16.f;
			
			// get raw acceleration values
			fAccX				= r.getAcceleration()[0];
			fAccY 				= r.getAcceleration()[1];
			fAccZ				= r.getAcceleration()[2];
			
			// square them up
			fAccX_2				= fAccX * fAccX;
			fAccY_2				= fAccY * fAccY;
			fAccZ_2				= fAccZ * fAccZ;

			// calculate the magnitude of the acceleration
			fMag = (float)Math.sqrt(fAccX_2 + fAccY_2 + fAccZ_2);

			fMagnitudeBuffer[iMagBufferIndex] = fMag;
			for(iIndex = 0; iIndex < iMagnitudeBufferSize; iIndex++)
			{
				fMovingAverageSum += fMagnitudeBuffer[iIndex];
			}
			fMovingAverage = fMovingAverageSum / iMovingAverageBufferSize;
			fMovingAverageBuffer[iMovingAverageBufferIndex] = fMovingAverage;
			
			// big bang condition for step detection 
			if ((fMovingAverageBuffer[(iMovingAverageBufferIndex + 3) % iMovingAverageBufferSize] > fThreshold) && 
				(fMovingAverageBuffer[(iMovingAverageBufferIndex + 3) % iMovingAverageBufferSize] > 
				 fMovingAverageBuffer[(iMovingAverageBufferIndex + 4) % iMovingAverageBufferSize])				&& 
				(fMovingAverageBuffer[(iMovingAverageBufferIndex + 3) % iMovingAverageBufferSize] > 
				 fMovingAverageBuffer[(iMovingAverageBufferIndex + 2) % iMovingAverageBufferSize]) 				&&
				(fMovingAverageBuffer[(iMovingAverageBufferIndex + 4) % iMovingAverageBufferSize] > 
				 fMovingAverageBuffer[(iMovingAverageBufferIndex + 5) % iMovingAverageBufferSize]) 				&&
				(fMovingAverageBuffer[(iMovingAverageBufferIndex + 2) % iMovingAverageBufferSize] > 
				 fMovingAverageBuffer[(iMovingAverageBufferIndex + 1) % iMovingAverageBufferSize]))
			{
				// we have detected a STEP !!
				iStepCount++;
				fAdjustmentFactor =  sharedPreference.getFloat("SENSITIVITY_ADJUSTMENT_FACTOR", 1.f);
				iAdjustedStepCount = (int) (fAdjustmentFactor * (float)iStepCount);
				handle.obtainMessage(0, (int)iAdjustedStepCount, 0).sendToTarget();

				// write the value into the Shared Preferences
				sharedPreference.edit().putInt("STEP_COUNT", (int)iAdjustedStepCount).commit();
			}
			
			// as the name says, write step count to file at the right time, and handle uploads too
			WriteAndUploadStepCount();
			
			iMagBufferIndex				= (iMagBufferIndex + 1) % iMagnitudeBufferSize;
			iMovingAverageBufferIndex	= (iMovingAverageBufferIndex + 1) % iMovingAverageBufferSize;
			return true;
		}		
	
	// function to write data to the file in an hourly fashion and upload the file too
	private void WriteAndUploadStepCount()
	{
		long lCurrentSystemTime 		= System.currentTimeMillis();
		long lLogWrittenTime 			= sharedPreference.getLong("LAST_FILE_WRITE_TIME", 0);
		
		// convert the "long" system time into the right formats
		Time currentTime 	= new Time();
		Time oldTime		= new Time(); 
		currentTime.set(lCurrentSystemTime);
		oldTime.set(lLogWrittenTime);
		
		if ( (lLogWrittenTime == 0) || (currentTime.hour != oldTime.hour))
		{
			// write out the last update time 
			sharedPreference.edit().putLong("LAST_FILE_WRITE_TIME", lCurrentSystemTime).commit();

			// write out the hourly update value to the SD card 
			// this function will automatically reset the step count
			WritePedometerValueToFile();
			
			// this function will upload data to the server 
			// if the upload was successful, the file will be deleted from the disk
			// if the upload was unsuccessful, the next try will happen after an hour
			UploadActivityFileToServer();
		}

	}
	
	// function to write the latest pedometer value to the file
	private void WritePedometerValueToFile()
	{
		String				strOutputToWrite 	= "";
		SimpleDateFormat 	dateFormat 			= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		long 				lCurrentSystemTime 	= System.currentTimeMillis();	 
		Date				date				= new Date(lCurrentSystemTime);
		String				strTimeStamp 		= dateFormat.format(date);
		
		strOutputToWrite = strPhoneNumber + "," + strTimeStamp + "," + iAdjustedStepCount + "\r\n";
		
		// look for the MACTM directory in external storage
		File pedometerDirectory = new File(Environment.getExternalStorageDirectory(), "MACTM");
		if (!pedometerDirectory.exists())					// create it if it does not exist
		{
			pedometerDirectory.mkdir();
		}
		
		// open the file 
		File pedometerFile = new File(pedometerDirectory.getPath(), "hourly_data.csv");
		try
		{
			FileWriter 		fileWriter 		= new FileWriter(pedometerFile, true);
			BufferedWriter  bufferedWriter 	= new BufferedWriter(fileWriter, 1024);
			bufferedWriter.append(strOutputToWrite);
			bufferedWriter.close();
			fileWriter.close();
			
			iStepCount 			= 0;	// reset the step count once it has been written to the file
			iAdjustedStepCount 	= 0;			 
											
		} 
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	// function to upload the activity file to the server 
	private void UploadActivityFileToServer()
	{
		Uploader fileUploader = new Uploader(sharedPreference);
		fileUploader.start();
		return;
	}
}
	// BROADCOAST RECEIVER definition
	private BroadcastReceiver mReceiver = new BroadcastReceiver() 
	{

		// FUNCTION: onReceive
		public void onReceive(Context context, Intent intent) 
		{
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) 
			{				
				mySensorManager.unregisterListener(mySensorListener);
				mySensorManager.registerListener(mySensorListener, sAccSensor,	ACC_SAMPLING_RATE);
				mySensorManager.registerListener(mySensorListener, sMagSensor,	ACC_SAMPLING_RATE);				
			}
			else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) 
			{
				// trap point for the screen ON event
			}			
		}
	};	
	
	// STEP DETECTED HANDLER DEFINITION
	Handler mHandler = new Handler() 
	{
		// FUNCTION: handleMessage
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case 0:	// 0 => step detected
				Intent intent = new Intent("edu.ucla.cs.m117.grad.DESKTOP_WIDGET_RCV");
				intent.putExtra("PedometerSteps", msg.arg1);		
				sendBroadcast(intent);
				break;			
			}
		}
	};
}