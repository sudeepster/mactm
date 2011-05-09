package edu.ucla.cs.m117.grad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

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

        if (toggleButton.isChecked()) {
            pedometerStateLabel.setText("Pedometer is On.");
        } else {
            pedometerStateLabel.setText("Pedometer is Off.");
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    pedometerStateLabel.setText("Pedometer is On.");
                } else {
                    pedometerStateLabel.setText("Pedometer is Off.");
                }
            }
        });

    Spinner spinner = (Spinner) findViewById(R.id.view_selector);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.views, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);


        //setContentView(R.layout.main);
    }
}
