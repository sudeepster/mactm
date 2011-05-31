package edu.ucla.cs.m117.grad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.ucla.cs.m117.grad.graphs.GraphView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import edu.ucla.cs.m117.grad.utils.MyOnItemSelectedListener;

public class MyActivity extends Activity
{
    private boolean bServicesStarted = false;
    private ISensorService2 iss2Pedometer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.pedometer_button);
        final TextView pedometerStateLabel = (TextView) findViewById(R.id.pedometer_label);

        toggleButton.setChecked(true);

        if (toggleButton.isChecked()) {
            pedometerStateLabel.setTextColor(R.color.green);
            pedometerStateLabel.setText("Pedometer is On.");
            changeWidgetView(true);
        } else {
            pedometerStateLabel.setTextColor(R.color.red);
            pedometerStateLabel.setText("Pedometer is Off.");
            changeWidgetView(false);
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    pedometerStateLabel.setTextColor(R.color.green);
                    pedometerStateLabel.setText("Pedometer is On.");
                    changeWidgetView(true);
                } else {
                    pedometerStateLabel.setTextColor(R.color.red);
                    pedometerStateLabel.setText("Pedometer is Off.");
                    changeWidgetView(false);
                }
            }
        });

		// @niranjanr 
        Intent intentPedometerSercice = new Intent();			        	// create the intent for the service
        intentPedometerSercice.setAction("edu.ucla.cs.m117.grad.MyService");// this "action" string comes from AndroidManifest.xml
        startService(intentPedometerSercice);								// call to start all services	
        MyBindService();													// bind the started service to this component 
        bServicesStarted = true;
    }

    public void changeWidgetView(boolean displayWidgets) {
        if (displayWidgets) {
            final LinearLayout pedoOnLayout = (LinearLayout)findViewById(R.id.pedoOn);
            final LinearLayout pedoOffLayout = (LinearLayout)findViewById(R.id.pedoOff);
            final LinearLayout graphLayout = (LinearLayout)findViewById(R.id.graphlayout);

            graphLayout.setVisibility(View.VISIBLE);
            pedoOffLayout.setVisibility(View.GONE);
            pedoOnLayout.setVisibility(View.VISIBLE);

            Spinner spinner = (Spinner) findViewById(R.id.view_selector);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.views, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new MyOnItemSelectedListener(getApplicationContext(), graphLayout));

            SeekBar sensitivity = (SeekBar) findViewById(R.id.sensitivity_calibrator);
            final TextView sensitivityValue = (TextView) findViewById(R.id.sensitivity_value);

            sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    sensitivityValue.setText(String.valueOf(i));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        } else {
            final LinearLayout pedoOnLayout = (LinearLayout)findViewById(R.id.pedoOn);
            final LinearLayout pedoOffLayout = (LinearLayout)findViewById(R.id.pedoOff);

            final TextView displayOff = (TextView) findViewById(R.id.pedometer_OFF_message);
            pedoOffLayout.setVisibility(View.VISIBLE);
            pedoOnLayout.setVisibility(View.GONE);
        }
    }
    // FUNCTION: OnDestroy
    public void onDestroy() 
    {
		super.onDestroy();				// destroy base classes
		MyUnRegisterCallbacks();		// unregister call backs
		MyUnBindService();				// release the created service
	}

    // FUNCTION: MyBindService
    // bind the service to this component 
    private void MyBindService()
    {
	    Intent intentBindService = new Intent("edu.ucla.cs.m117.grad.PedometerSensorService");
	    bindService(intentBindService, sscPedometer, Context.BIND_AUTO_CREATE);
    }
  
    // FUNCTION: MyUnBindService
    // unbind the service at the end
    private void MyUnBindService()
    {
    	if(bServicesStarted)
    	{
    		unbindService(sscPedometer);
    	}
    }

    // FUNCTION: UnRegisterCallbacks
    // Release the earlier registered Callback
    // -- the exception must be caught!
    private void	MyUnRegisterCallbacks()
    {
		try 
		{
			if(bServicesStarted)
			{
				iss2Pedometer.unregisterCallback(mCallback);
			}
		} 
		catch (RemoteException re) 
		{
			re.printStackTrace();
		}
    }
    
    // Create the connection between the service and this activity component
    private ServiceConnection sscPedometer = new ServiceConnection() 
    {
    	// FUNCTION: OnServiceConnected
    	// -- exception must be handled within try catch
    	public void onServiceConnected(ComponentName name, IBinder service) 
    	{
    		iss2Pedometer = ISensorService2.Stub.asInterface(service);	
    		try 
    		{			
    			iss2Pedometer.registerCallback(mCallback);
    		} 
    		catch (RemoteException e) 
    		{				
    			e.printStackTrace();
    		}
    	}

    	// FUNCTION: OnServiceDisconnected
    	// -- just set the object to null
    	public void onServiceDisconnected(ComponentName name) 
    	{
    		iss2Pedometer = null;			
    	}		
    };
    
    // ICallback Interface 
    private ICallBack mCallback = new ICallBack.Stub()
    {
	};
}
