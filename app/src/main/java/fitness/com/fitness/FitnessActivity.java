package fitness.com.fitness;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.result.DataTypeResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.wearable.DataApi;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;
import com.google.android.gms.fitness.data.DataSource;
import static com.google.android.gms.fitness.data.DataType.AGGREGATE_INPUT_TYPES;


public class FitnessActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener,OnClickListener {

    private GoogleApiClient mClient = null;
    private GoogleApiClient mFitnessClient = null;

    public static final String TAG = "FitnessApi";

    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    private String strJson = null;

    private Toolbar toolbar;
    private String json;

    private String infoRead = "false";

    private final int PROFILE_PIC_SIZE = 150;

    private  OutputStreamWriter out = null;

    private  HttpURLConnection httpCon;

    private List<Device> devices = new ArrayList<Device>();


    private String email;


    SampleAlarmReceiver alarmReceiver = new SampleAlarmReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        infoRead = pref.getString("infoRead", "false");
        Log.i("infoRead", infoRead);


        if(!Boolean.valueOf(infoRead)){
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }else{
            toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
            setSupportActionBar(toolbar);

            // This method sets up our custom logger, which will print all log messages to the device
            // screen, as well as to adb logcat.

            buildFitnessClient();

    /*    GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(3);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setMaxY(10);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 5),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        series.setColor(Color.RED);


        graph.addSeries(series); */
        }







    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Plus.API)
                .useDefaultAccount()
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(Fitness.SCOPE_LOCATION_READ)
                .addScope(Fitness.SCOPE_ACTIVITY_READ)
                .addScope(Fitness.SCOPE_BODY_READ_WRITE)
                 .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        mFitnessClient = new GoogleApiClient.Builder(this)

                .addApi(Fitness.CONFIG_API)

                .useDefaultAccount()

                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)

                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed" + FitnessStatusCodes.getStatusCodeString(connectionResult.getErrorCode()));
        try {
            connectionResult.startResolutionForResult(FitnessActivity.this, 1);
        }catch (IntentSender.SendIntentException e) {
            Log.e(TAG,
                    "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onClick(View v) {

       if(v.getId()==R.id.linearlayout3){

           Intent intent = new Intent(this, FeedbackActivity.class);
           startActivity(intent);
       // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         //   if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           //     startActivityForResult(takePictureIntent, 1);
            //}
        }

    }


    private class GETDeviceData extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {


            // Begin by creating the query.

            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.




            Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                    .setDataTypes(
                            DataType.TYPE_LOCATION_SAMPLE,
                            DataType.TYPE_STEP_COUNT_DELTA,
                            DataType.TYPE_DISTANCE_DELTA,
                            DataType.TYPE_HEART_RATE_BPM )
                    .setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED)

                    .build())
                    .setResultCallback(new ResultCallback() {

                        @Override
                        public void onResult(Result dataSourcesResult) {

                          Log.i(TAG, ((DataSourcesResult) dataSourcesResult).getDataSources().toString());

                            for (DataSource dataSource : ((DataSourcesResult) dataSourcesResult).getDataSources()) {
                                com.google.android.gms.fitness.data.Device device = dataSource.getDevice();
                                String fields = dataSource.getDataType().getFields().toString();

                                Device deviceDetails = new Device();

                                deviceDetails.setDeviceId(device.getModel());
                                devices.add(deviceDetails);
                                Log.i(TAG, deviceDetails.getDeviceId());

                            }

                        }
                    });
            // [END read_dataset]

            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.

            return null;
        }
    }

    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {


            // Begin by creating the query.
            DataReadRequest readRequest = queryFitnessData();

            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            // [END read_dataset]

            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
            printData(dataReadResult);

            return null;
        }
    }



    private class InsertUserData extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {


            updateCustomerDetails();
            return null;
        }
    }


    private class GetBalanceData extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... params){
            try {
                  strJson = loadFromNetwork("http://fitnesspayseclipse.mybluemix.net/api/fitnesspays/customer/fitnessstats?_id="+email);
            Log.i(TAG,strJson);
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                    TextView text = (TextView)findViewById(R.id.balance);
                         Gson gson = new Gson();
                       Statistics obj = gson.fromJson(strJson, Statistics.class);
                  text.setText("€"+obj.getBalance());

                    }
                });


                   }catch(IOException exc){
                     Log.e(TAG,exc.getMessage());
                 }
            return null;
        }
    }


    private class GetInsuranceDiscount extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... params){
            try {
                strJson = loadFromNetwork("http://fitnesspayseclipse.mybluemix.net/api/fitnesspays/customer/fitnesspremium?_id="+email);
                Log.i(TAG,strJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView)findViewById(R.id.discount);
                        Gson gson = new Gson();
                        Premium obj = gson.fromJson(strJson, Premium.class);
                        text.setText(obj.getPremiumValue()+"%");

                    }
                });


            }catch(IOException exc){
                Log.e(TAG,exc.getMessage());
            }
            return null;
        }
    }

    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -2);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));
        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // [END build_read_data_request]

        return readRequest;
    }

    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataPoints());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        Device device = null;

        for (com.google.android.gms.fitness.data.DataPoint dp : dataSet.getDataPoints()) {

            if(dp.getOriginalDataSource()!=null) {
                device = new Device();
                device.setDeviceId(dp.getOriginalDataSource().getDevice().toString());
                Log.i(TAG, "Data point:" + dp.getOriginalDataSource().getDevice());
                devices.add(device);
            }
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }


     //   try {
          //  strJson = loadFromNetwork("http://fitnesspayseclipse.mybluemix.net/api/favorites/fitness");

//            Log.i(TAG,strJson);
         //   runOnUiThread(new Runnable() {
           //     @Override
           //     public void run() {
                //    TextView text = (TextView)findViewById(R.id.labelaccount);
           //         Gson gson = new Gson();
             //       SampleObject obj = gson.fromJson(strJson, SampleObject.class);
                  //  text.setText(obj.getBody()[0].getName());

            //    }
            //});

     //
     //   }catch(IOException exc){
       //     Log.e(TAG,exc.getMessage());
       // }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitness, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_data) {
            return true;
        }

        if(id==R.id.action_settings){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(takePictureIntent, 1);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected!!!");
        // Now you can make calls to the Fitness APIs.  What to do?
        // Look at some data!!

               //    TextView text = (TextView)findViewById(R.id.labelaccount);
                //  text.setText(obj.getBody()[0].getName());




             if (Plus.PeopleApi.getCurrentPerson(mClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mClient);

           String personPhotoUrl = currentPerson.getImage().getUrl();
          // Bitmap bm = BitmapFactory.decodeStream(personPhoto.);
           //Drawable d = new BitmapDrawable(bm);
           ImageView image = (ImageView)findViewById(R.id.profileImage);
           new LoadProfileImage(image).execute(personPhotoUrl.substring(0,
                   personPhotoUrl.length() - 2)
                   + PROFILE_PIC_SIZE);

        }
        alarmReceiver.setAlarm(this);

    //    new GETDeviceData().execute();


        new InsertAndVerifyDataTask().execute();
        new InsertUserData().execute();
        new GetBalanceData().execute();
        new GetInsuranceDiscount().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    private void updateCustomerDetails() {

        email = Plus.AccountApi.getAccountName(mClient);



        try {

            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mClient);
            Account account = new Account();

            LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            //LocationListener listener = new MyLocationListener();
          //  manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,listener);
            Location location =  manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            account.setLocation("lattitude :" +location.getLatitude() + " longitude :" +location.getLongitude());

          //  account.setLocation(currentPerson.getPlacesLived().get(0).getValue());
         //   Log.i(TAG,currentPerson.getPlacesLived().get(0).toString());
            account.set_id(email);
            Customer customer = new Customer();
            customer.setName(currentPerson.getDisplayName());
            account.setCustomer(customer);


            Device device = new Device();
            StringBuffer stringBuffer = new StringBuffer();
            for(Device deviceDetails : devices){
                stringBuffer.append(deviceDetails.getDeviceId());
            }

            device.setDeviceId(stringBuffer.toString());
            account.setDevice(device);
            Gson gson = new Gson();
            json = gson.toJson(account);

            URL url = new URL("http://fitnesspayseclipse.mybluemix.net/api/fitnesspays/customer/account");
            httpCon = (HttpURLConnection) url.openConnection();

            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            httpCon.setRequestProperty("Content-Type", "application/json");
            httpCon.setRequestProperty("Accept", "application/json");


            OutputStreamWriter out = new OutputStreamWriter(
                                httpCon.getOutputStream());
                        out.write(json);
                        out.close();


            Log.i(TAG, String.valueOf(httpCon.getResponseCode()));
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the app is not already connected or attempting to connect
        if (resultCode == RESULT_OK) if (!mClient.isConnecting() && !mClient.isConnected()) {
            mClient.connect();
        }
        Log.i(TAG ,String.valueOf(requestCode));
        if (requestCode == 1 && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView image = (ImageView)findViewById(R.id.profileImage);
                image.setImageBitmap(imageBitmap);
            }


        //Log.i(TAG,data.getDataString());

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


    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            Log.i(TAG,urls[0]);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
//            CircleDrawable circle = new CircleDrawable(result);
            //bmImage.setBackground(circle);
            bmImage.setImageBitmap(result);

        }
    }


}

