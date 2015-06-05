package fitness.com.fitness;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.camera2.CameraManager;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.hardware.Camera;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class FeedbackActivity extends ActionBarActivity {

    public final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;

    private  String strJson;

    byte[] imageBytes ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // do we have a camera?
        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);


        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }
        }
    }

    public void onClick(View view) {
        camera.takePicture(null, null,
                new PhotoHandler(getApplicationContext()));
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    private class PhotoHandler implements Camera.PictureCallback {

        private final Context context;

        public PhotoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

         //   File pictureFileDir = getDir();

        //    if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

        //        Log.d(FeedbackActivity.DEBUG_TAG, "Can't create directory to save image.");
        //        Toast.makeText(context, "Can't create directory to save image.",
         //               Toast.LENGTH_LONG).show();
            //    return;

         //   }
            imageBytes = data;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            String date = dateFormat.format(new Date());
            String photoFile = "Picture_" + date + ".jpg";

       //     String filename = pictureFileDir.getPath() + File.separator + photoFile;

      //      File pictureFile = new File(filename);

            try {
                Bitmap imageBitmap = (Bitmap) BitmapFactory.decodeByteArray(data, 0, data.length);
           //     ImageView image = (ImageView)findViewById(R.id.imageView3);
           //     image.setImageBitmap(imageBitmap);





                new GetFeedback().execute();


             //   findViewById(R.id.imageView3)

               // FileOutputStream fos = new FileOutputStream(pictureFile);
               // fos.write(data);
               // fos.close();
                Toast.makeText(context, "New Image saved:" + photoFile,
                        Toast.LENGTH_LONG).show();
            } catch (Exception error) {
           //     Log.d(FeedbackActivity.DEBUG_TAG, "File" + filename + "not saved: "
         //               + error.getMessage());
                Toast.makeText(context, "Image could not be saved.",
                        Toast.LENGTH_LONG).show();
            }
        }

        private File getDir() {
            File sdDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            return new File(sdDir, "CameraAPIDemo");
        }


    }


    private class GetFeedback extends AsyncTask<Void,Void,Void> {
        protected Void doInBackground(Void... params){

            String apiURL = "https://api.eu.apim.ibmcloud.com/edwardciggaarnlibmcom2/sb/facereader/readface?client=3b6c7b8a-435b-4d91-87de-2baf8962e41a&clientSecret=D2qN1xG3tE4aU6iL0cL0pS8yC3yD1eX5kC3fL2dR1rU3mP8dU1&GetFacialStates&&ImageUrl=https://thetonygrands.files.wordpress.com/2013/09/happy-face-girl-photo.jpg";
            try {


            //    HttpURLConnection httpCon = (HttpURLConnection) apiURL.openConnection();

              //  httpCon.setDoOutput(true);
               // httpCon.setRequestMethod("PUT");
                //httpCon.setRequestProperty("Content-Type", "multipart/form-data");
                //httpCon.setRequestProperty("Accept", "image/jpg");


               // InputStream stream = new ByteArrayInputStream(imageBytes);


//                OutputStreamWriter out = new OutputStreamWriter(
  //                      httpCon.getOutputStream());
               //out.write(imageBytes,0,imageBytes.length);
    //            out.close();


               strJson = loadFromNetwork(apiURL);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {



                      try {
                          JSONObject jsonObj = new JSONObject(strJson);
                         String expression =  jsonObj.getString("DominantExpression");

                          Log.i(DEBUG_TAG,expression);

                          Toast.makeText(FeedbackActivity.this, "You look " +expression, Toast.LENGTH_LONG)
                                  .show();

                      }catch (JSONException e){
                          e.printStackTrace();
                      }


                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
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

}
