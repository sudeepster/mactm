package edu.ucla.cs.m117.grad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.ucla.cs.m117.grad.graphs.GraphView;

import java.util.Date;

public class MyActivity extends Activity
{
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
    }

    public void changeWidgetView(boolean displayWidgets) {
        if (displayWidgets) {
            final LinearLayout pedoOnLayout = (LinearLayout)findViewById(R.id.pedoOn);
            final LinearLayout pedoOffLayout = (LinearLayout)findViewById(R.id.pedoOff);
            pedoOffLayout.setVisibility(View.GONE);
            pedoOnLayout.setVisibility(View.VISIBLE);

            Spinner spinner = (Spinner) findViewById(R.id.view_selector);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.views, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

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

            float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
            String[] verlabels = new String[] { "great", "ok", "bad" };
            String[] horlabels = new String[] { "today", "tomorrow", "next week", "next month" };
            GraphView graphView = new GraphView(this, values, "GraphViewDemo",horlabels, verlabels, GraphView.LINE);
            addContentView(graphView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    150));
        } else {
            final LinearLayout pedoOnLayout = (LinearLayout)findViewById(R.id.pedoOn);
            final LinearLayout pedoOffLayout = (LinearLayout)findViewById(R.id.pedoOff);

            final TextView displayOff = (TextView) findViewById(R.id.pedometer_OFF_message);
            pedoOffLayout.setVisibility(View.VISIBLE);
            pedoOnLayout.setVisibility(View.GONE);
        }
    }
}
