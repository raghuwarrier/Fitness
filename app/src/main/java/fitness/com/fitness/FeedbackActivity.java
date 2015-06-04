package fitness.com.fitness;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.camera2.CameraManager;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.hardware.Camera;


public class FeedbackActivity extends ActionBarActivity {

    public final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;

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

            File pictureFileDir = getDir();

            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

                Log.d(FeedbackActivity.DEBUG_TAG, "Can't create directory to save image.");
                Toast.makeText(context, "Can't create directory to save image.",
                        Toast.LENGTH_LONG).show();
                return;

            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            String date = dateFormat.format(new Date());
            String photoFile = "Picture_" + date + ".jpg";

            String filename = pictureFileDir.getPath() + File.separator + photoFile;

            File pictureFile = new File(filename);

            try {
                Bitmap imageBitmap = (Bitmap) BitmapFactory.decodeByteArray(data, 0, data.length);
                ImageView image = (ImageView)findViewById(R.id.imageView3);
                image.setImageBitmap(imageBitmap);


             //   findViewById(R.id.imageView3)

               // FileOutputStream fos = new FileOutputStream(pictureFile);
               // fos.write(data);
               // fos.close();
                Toast.makeText(context, "New Image saved:" + photoFile,
                        Toast.LENGTH_LONG).show();
            } catch (Exception error) {
                Log.d(FeedbackActivity.DEBUG_TAG, "File" + filename + "not saved: "
                        + error.getMessage());
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

}
