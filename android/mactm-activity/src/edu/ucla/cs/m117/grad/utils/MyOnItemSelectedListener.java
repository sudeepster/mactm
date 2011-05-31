package edu.ucla.cs.m117.grad.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import edu.ucla.cs.m117.grad.communications.Communicator;
import edu.ucla.cs.m117.grad.graphs.GraphView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;

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
        String response = communicator.process(selected);
        //float[] values = stringToFloatArray(response);

        String[] verlabels = new String[] { "awesome", "great", "good", "ok", "bad" };


        linearLayout.removeAllViews();

        Calendar cal = Calendar.getInstance();
        GraphView graphView = null;

        if (selected.equalsIgnoreCase("daily")) {
            String[] horlabels = new String[] { "6:00", /*"7:00", "8:00",*/ "9:00", /*"10:00", "11:00", "12:00",*/ "1:00", /*"2:00", "3:00", "4:00", "5:00",*/ "6:00",/* "7:00", "8:00", "9:00",*/ "10:00" };
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //Test data
            float[] vals = {300, 200, 150, 300, 400, 350, 250, 275, 200, 215, 123, 200, 300, 200, 150, 300, 400};

            graphView = new GraphView(this.context, vals, dateFormat.format(cal.getTime()), horlabels, verlabels, GraphView.LINE);
        } else if (selected.equalsIgnoreCase("weekly")) {
            float[] vals = {300, 200, 150, 300, 400, 350, 250};

            String[] horlabels = new String[] {"Sun", "Mon", "Tues", "Wed", "Thru", "Fri", "Sat"};
            int weekofyear = cal.get(Calendar.WEEK_OF_YEAR);
            graphView = new GraphView(this.context, vals, Integer.toString(weekofyear) + " week", horlabels, verlabels, GraphView.LINE);
        } else if (selected.equalsIgnoreCase("monthly")) {
            String[] strMonths = new String[]{
                                                        "January",
                                                        "February",
                                                        "March",
                                                        "April",
                                                        "May",
                                                        "June",
                                                        "July",
                                                        "August",
                                                        "September",
                                                        "October",
                                                        "November",
                                                        "December"
                                                        };

            int month = cal.get(Calendar.MONTH);

            int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            String[] horlabels = new String[days];
            float[] vals = new float[days];

            Random generator = new Random();
            for (int idx = 0; idx < days; ++idx) {
               /* if (idx%5 == 0) */
               horlabels[idx] = Integer.toString(idx + 1);
                vals[idx] = generator.nextFloat() * 450;
            }
            graphView = new GraphView(this.context, vals, strMonths[month], horlabels, verlabels, GraphView.LINE);
        }

        linearLayout.addView(graphView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private float[] stringToFloatArray(String data) {
        if (data.equalsIgnoreCase("Error"))  {
            return null;
        }

        String[] tokens = data.split("\\s");

        float[] ret = new float[tokens.length];
        int i = 0;
        for (String token : tokens) {
            float f = Float.parseFloat(token);
            ret[i] = f;
            ++i;
        }

        return ret;
    }
}
