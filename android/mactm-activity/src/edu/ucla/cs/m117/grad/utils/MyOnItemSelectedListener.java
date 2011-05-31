package edu.ucla.cs.m117.grad.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import edu.ucla.cs.m117.grad.communications.Communicator;

/**
 * Created by IntelliJ IDEA.
 * User: spradhan
 * Date: 5/30/11
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private Context context;

    public MyOnItemSelectedListener(Context context) {
        this.context = context;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //To change body of implemented methods use File | Settings | File Templates.
        String selected = adapterView.getItemAtPosition(i).toString();
        Communicator communicator = new Communicator(context);
        communicator.process(selected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
