package fitness.com.fitness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SavingsGraphActivity extends ActionBarActivity {

    public static final String TAG = "SavingsGraphActivity";

    private Toolbar toolbar;

    private String email;

    private String strJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_graph);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            email= extras.getString("email");
        }
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_savings_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetBalanceData extends AsyncTask<Void,Void,Void> {
        protected Void doInBackground(Void... params){
            try {
                strJson = loadFromNetwork("http://fitnesspaysapp.eu-gb.mybluemix.net/api/fitnesspays/customer/insurance/history?_id="+email);
                Log.i(TAG,strJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        SavingsList obj = gson.fromJson(strJson, SavingsList.class);

                        createGraph(obj);

                    }
                });


            }catch(IOException exc){
                Log.e(TAG,exc.getMessage());
            }
            return null;
        }
    }

    public String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }


    public void createGraph(SavingsList savings){
        GraphView graph = (GraphView) findViewById(R.id.graph);

        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(getResources().getColor(R.color.colorPrimaryDark));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(getResources().getColor(R.color.colorPrimaryDark));
        graph.getGridLabelRenderer().setVerticalAxisTitle("Balance over months");
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    switch ((int) value) {
                        case 0:
                            return "Jan";
                        case 1:
                            return "Feb";
                        case 2:
                            return "Mar";
                        case 3:
                            return "Apr";
                        case 4:
                            return "May";

                    }
                    return super.formatLabel(value, isValueX);
                } else {
                    return super.formatLabel(value, isValueX) + " €";
                }
            }


        });

        DataPoint[] dataPoints = new DataPoint[savings.getSavings().size()];
        int i=0;
        for(Savings saving : savings.getSavings()){
            Log.i(TAG,saving.getPeriodValueName());
            DataPoint dataPoint = new DataPoint(getTimeUnit(saving.getPeriodValueName()),Double.valueOf(saving.getBalance()));
            dataPoints[i++] = dataPoint;

        }


        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);


        series.setColor(Color.RED);


        graph.addSeries(series);
    }
    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {

        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    /**
     * Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from www.google.com.
     * @return String version of InputStream.
     * @throws IOException
     */
    private String readIt(InputStream stream) throws IOException {

        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for(String line = reader.readLine(); line != null; line = reader.readLine())
            builder.append(line);
        reader.close();
        return builder.toString();
    }

    private int getTimeUnit(String month){
        if(month.equals("January")){
            return 0;
        }else if(month.equals("February")){
            return 1;
        }else if(month.equals("March")){
            return 2;
        }else if(month.equals("April")){
            return 3;
        }else if(month.equals("May")){
            return 4;
        }else if(month.equals("June")){
            return 5;
        }else if(month.equals("July")){
            return 6;
        }else if(month.equals("August")){
            return 7;
        }else if(month.equals("September")){
            return 8;
        }else if(month.equals("October")){
            return 9;
        }else if(month.equals("November")){
            return 10;
        }else if(month.equals("December")){
            return 11;
        }
        return 0;
    }

}
