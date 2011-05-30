// ActivityWidget implementation
package edu.ucla.cs.m117.grad;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

// this is the actual class definition for the widget
// it extends the android provided AppWidgetProvider class
public class ActivityWidget extends AppWidgetProvider 
{	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		return;
		// leave this empty for now
	}
	
	// FUNCTION: onReceive (trap the broadcasts of 'intent'
	public void onReceive(Context context, Intent intent) 
	{		
		int 	iSteps 			= 0;
		int[] 	iAppWidgetIds 	= null;
		int		iWidgetCount 	= 0;
		int		iIndex 			= 0;	
		int		iAppWidgetId	= 0;
		
		super.onReceive(context, intent);
		if(!intent.getAction().equals("edu.ucla.cs.m117.grad.DESKTOP_WIDGET_RCV"))
		{
			return;
		}

		// get the step count from the update intent
		iSteps = intent.getIntExtra("PedometerSteps", 0);
		
		// get instance of app widget manager
		AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
		
		// Retrieve the identifiers for each instance of your chosen widget.
		ComponentName thisWidget = new ComponentName(context, ActivityWidget.class);
		// get the ids for all instances of this widget that might have been created
		iAppWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);		
		// get a count of the widget instances
		iWidgetCount = iAppWidgetIds.length;
		
		// Iterate through each widget, creating a RemoteViews object and
		// applying the modified RemoteViews to each widget.
		for (iIndex = 0; iIndex < iWidgetCount; iIndex++) 
		{
			iAppWidgetId = iAppWidgetIds[iIndex];
			// get handle to remove views object
			RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.desktop_widget);
						
			// Notify the App Widget Manager to update the widget using
			// the modified remote view.
			views.setTextViewText(R.id.widget_stepsview,"" + iSteps);
			
			// get pending intent related to the main activity
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MyActivity.class), 0);
			views.setOnClickPendingIntent(R.id.desktop_widget_image, pendingIntent);
			appWidgetManager.updateAppWidget(iAppWidgetId, views);
		}			
	}
}