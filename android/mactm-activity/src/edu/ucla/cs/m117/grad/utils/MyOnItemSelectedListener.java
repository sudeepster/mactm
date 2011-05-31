package edu.ucla.cs.m117.grad.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import edu.ucla.cs.m117.grad.communications.Communicator;
import edu.ucla.cs.m117.grad.graphs.GraphView;

/**
 * Created by IntelliJ IDEA.
 * User: spradhan
 * Date: 5/30/11
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private Context context;
    private LinearLayout linearLayout;

    public MyOnItemSelectedListener(Context context, LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //To change body of implemented methods use File | Settings | File Templates.
        String selected = adapterView.getItemAtPosition(i).toString();
        Communicator communicator = new Communicator(context);
        communicator.process(selected);
        float[] values = new float[] { 2.0f,1.5f, 2.5f, 1.0f , 3.0f };
        String[] verlabels = new String[] { "great", "ok", "bad" };
        String[] horlabels = new String[] { "today", "tomorrow", "next week", "next month" };

        linearLayout.removeAllViews();

        GraphView graphView = null;
        if (selected.equalsIgnoreCase("daily")) {
            graphView = new GraphView(this.context, values, "GraphViewDemo", horlabels, verlabels, GraphView.LINE);
        } else {
             graphView = new GraphView(this.context, values, "GraphViewDemo", horlabels, verlabels, GraphView.BAR);
        }

        linearLayout.addView(graphView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
