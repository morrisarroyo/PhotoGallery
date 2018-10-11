package a00950540.bcit.ca.photogallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.icu.text.SimpleDateFormat;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    public static final int FILTER_ACTIVITY_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int SEARCH_CAPTION_ACTIVITY_REQUEST_CODE = 2;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 3;

    public static final int DATE_STRING_LENGTH = 8;
    public static final int DATE_TIME_STRING_LENGTH = 14;


    private String currentPhotoPath = null;
    private int currentPhotoIndex = 0;
    private ArrayList<String> photoGallery;
    private LocationManager locationManager;


    GalleryHelper helper;
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLeft      = (Button)findViewById(R.id.button_previous);
        Button btnRight     = (Button)findViewById(R.id.button_next);
        Button btnFilter    = (Button)findViewById(R.id.button_filter);
        Button btnSnap      = (Button)findViewById(R.id.button_snap);
        Button btnSave      = (Button)findViewById(R.id.button_save);
        Button btnReset     = (Button)findViewById(R.id.button_reset_gallery);
        et = (EditText) findViewById(R.id.editText_caption);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnFilter.setOnClickListener(filterListener);
        btnSnap.setOnClickListener(snapListener);
        btnSave.setOnClickListener(saveListener);
        btnReset.setOnClickListener(resetListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        helper = new GalleryHelper();
        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoGallery = helper.populateGallery(minDate, maxDate);
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0)
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
        displayPhoto(currentPhotoPath);
        et.setText(helper.getCaption(currentPhotoPath));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return;
    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, FilterActivity.class);
            startActivityForResult(i, FILTER_ACTIVITY_REQUEST_CODE);
        }
    };

    private View.OnClickListener snapListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("button_snap", "button_snap onclick");
            currentPhotoPath = helper.takePicture(v, MainActivity.this);
        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("button_save", "button_save onclick");
            EditText et = (EditText) findViewById(R.id.editText_caption);
            helper.renamePhoto(et.getText().toString().trim()
                    , photoGallery
                    , currentPhotoPath
                    , currentPhotoIndex
                    , MainActivity.this);
        }
    };

    private View.OnClickListener resetListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("button_reset", "button_res   et onclick");
            photoGallery = helper.resetGallery(photoGallery);
        }
    };


    public void onClick( View v) {
        switch (v.getId()) {
            case R.id.button_previous:
                --currentPhotoIndex;
                Log.d("button_previous"," Previous Image");
                break;
            case R.id.button_next:
                ++currentPhotoIndex;
                Log.d("button_next"," Next Image");
                break;
            default:
                break;
        }

        if (photoGallery.size() > 0) {
            if (currentPhotoIndex < 0)
                currentPhotoIndex = 0;
            if (currentPhotoIndex >= photoGallery.size())
                currentPhotoIndex = photoGallery.size() - 1;

            currentPhotoPath = photoGallery.get(currentPhotoIndex);

            Log.d("photosave, size", helper.getCaption(currentPhotoPath));
            Log.d("photoleft, size", Integer.toString(photoGallery.size()));
            Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
            displayPhoto(currentPhotoPath);
        }
    }

    public void goToSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToDisplay(String x) {
        Intent i = new Intent(this, DisplayActivity.class);
        i.putExtra("DISPLAY_TEXT", x);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "Enter");
        if (requestCode == FILTER_ACTIVITY_REQUEST_CODE) {
            Log.d("filterActivityRequest", "Enter");

            if (resultCode == RESULT_OK) {
                Log.d("filterActivityRequest", "Result_ok");

                String caption = data.getStringExtra("caption");
                Date dateFrom = new Date(Long.MIN_VALUE);
                Date dateTo = new Date(Long.MAX_VALUE);
                photoGallery = helper.populateGallery(dateFrom, dateTo);
                if (caption.length() > 0)
                    photoGallery = helper.filterGalleryByCaption(caption, photoGallery);

                String dateFromStr = data.getStringExtra("dateFrom");
                if (dateFromStr.length() == DATE_STRING_LENGTH) {
                    Log.d("filterActivityRequest", "getStringExtraDateFrom" + dateFromStr);
                    dateFrom = helper.getDate(dateFromStr);
                } else if (dateFromStr.length() == DATE_TIME_STRING_LENGTH) {
                    dateFrom = helper.getDateTime(dateFromStr);
                    Log.d("filterActivityRequest","getDateTime" +  dateFrom.toString());
                }

                String dateToStr = data.getStringExtra("dateTo");
                if (dateToStr.length() == DATE_STRING_LENGTH) {
                    Log.d("filterActivityRequest","getStringExtraDateTo" +  dateToStr);
                    dateTo = helper.getDate(dateToStr);
                } else if (dateToStr.length() == DATE_TIME_STRING_LENGTH) {
                    dateTo = helper.getDateTime(dateToStr);
                    Log.d("filterActivityRequest","getDateTime" +  dateTo.toString());
                }
                photoGallery = helper.filterGalleryByDate(dateFrom, dateTo, photoGallery);

                String latitudeStr = data.getStringExtra("latitude");
                float latitude = 0f;
                if (latitudeStr.length() > 0) {
                    Log.d("filterActivityRequest", "getStringExtraDateFrom" + dateFromStr);
                    latitude = Float.parseFloat(latitudeStr);
                    photoGallery = helper.filterGalleryByLatitude(latitude, photoGallery);
                }

                String longitudeStr = data.getStringExtra("longitude");
                float longitude = 0f;
                if (longitudeStr.length() > 0) {
                    Log.d("filterActivityRequest","getStringExtraDateTo" +  dateToStr);
                    longitude = Float.parseFloat(longitudeStr);
                    photoGallery = helper.filterGalleryByLongitude(longitude, photoGallery);
                }

                if(photoGallery.size() > 0) {
                    currentPhotoIndex = 0;
                    currentPhotoPath = photoGallery.get(currentPhotoIndex);
                    displayPhoto(currentPhotoPath);
                } else {
                    displayEmptyPhoto();
                }
            }
        }
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", data.getStringExtra("dateFrom"));
                Log.d("createImageFile", data.getStringExtra("dateTo"));
                String myFormat = "yyyyMMdd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Date dateFrom;
                Date dateTo;
                try {
                    dateFrom = sdf.parse(data.getStringExtra("dateFrom"));
                } catch (ParseException pe) {
                    dateFrom = new Date(Long.MIN_VALUE);
                }
                try {
                    dateTo = sdf.parse(data.getStringExtra("dateTo"));
                } catch (ParseException pe) {
                    dateTo = new Date(Long.MAX_VALUE);
                }
                photoGallery = helper.populateGallery(dateFrom, dateTo);
                Log.d("onCreate, size", Integer.toString(photoGallery.size()));
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }
        if (requestCode == SEARCH_CAPTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String caption = data.getStringExtra("caption");
                Date dateFrom = new Date(Long.MIN_VALUE);
                Date dateTo = new Date(Long.MAX_VALUE);
                photoGallery = helper.populateGallery(dateFrom, dateTo);
                photoGallery = helper.filterGalleryByCaption(caption, photoGallery);
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", "Picture Taken");
                if (currentPhotoPath.length() > 0) {
                    photoGallery.add(0, currentPhotoPath);

                }
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }
        Log.d("onActivityResult", "Exit");

    }

/*
    private void resetGallery()
    {
        Date dateFrom = new Date(Long.MIN_VALUE);
        Date dateTo = new Date(Long.MAX_VALUE);
        photoGallery = populateGallery(dateFrom, dateTo);
        currentPhotoIndex = 0;
        currentPhotoPath = photoGallery.get(currentPhotoIndex);
        displayPhoto(currentPhotoPath);
    }
*/
/*
    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/a00950540.bcit.ca.photogallery/files/Pictures");
        photoGallery = new ArrayList<String>();
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        File[] fList = file.listFiles();


        if (fList != null) {
            for (File f : file.listFiles()) {
////
                String dateStr = "";
                String[] split = f.getPath().split("/");
                split = split[split.length - 1].split("_");
                dateStr = split[2];
                Date date;
                try {
                    date = sdf.parse(dateStr);
                } catch (ParseException pe) {
                    date = new Date(Long.MIN_VALUE);
                }
                if (date.after(minDate) || date.before(maxDate)) {
////
                    photoGallery.add(f.getPath());
               // }
            }
        }
        Collections.reverse(photoGallery);
        return photoGallery;
    }
    */

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));

        EditText et = (EditText) findViewById(R.id.editText_caption);
        et.setText(helper.getCaption(path));
        String lat = "";
        String lng = "";
        try {
            ExifInterface exif = new ExifInterface(path);
            float[] latLong = new float[2];
            //lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            exif.getLatLong(latLong);
            //lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            lat = latLong[0] + "";
            lng = latLong[1] + "";
        } catch (IOException ioe)
        {
            lat = "Latitude Unavailable";
            lng = "Longitude Unavailable";
        }


        TextView tvLat = (TextView) findViewById(R.id.text_value_latitude);
        TextView tvLng = (TextView) findViewById(R.id.text_value_longitude);
        //tvLat.setText(String.valueOf(location.getLatitude()));
        //tvLng.setText(String.valueOf(location.getLongitude()));
        tvLat.setText(lat);
        tvLng.setText(lng);

        String date = "";

        TextView dateValue = (TextView) findViewById(R.id.text_value_date_gallery);
        dateValue.setText(helper.getDateTimeStringFromPath(path));

        TextView indexValue = (TextView) findViewById(R.id.text_value_index_gallery);
        indexValue.setText(photoGallery.indexOf(path) + "");
    }

    private void displayEmptyPhoto() {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageResource(R.mipmap.ic_launcher);
        EditText et = (EditText) findViewById(R.id.editText_caption);
        et.setText("No Image Found");
    }
/*
    private void renamePhoto(String caption) {
        String renameString = "";
        File image = new File(currentPhotoPath);
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String[] split = currentPhotoPath.split("/");
        Log.d("renamePhoto",split[split.length - 1] );
        split = split[split.length - 1].split("_");
        split[1] = caption;
        Log.d("renamePhoto", "Caption: " + caption);
        for (int i = 1; i < split.length; ++i) {
            renameString += "_" + split[i];
        }

        File renameImage = new File(dir,  renameString);
        if (image.renameTo(renameImage)) {
            photoGallery.set(currentPhotoIndex, renameImage.getAbsolutePath());
            currentPhotoPath = renameImage.getAbsolutePath();
        } else
        {
            Log.d("renamePhoto", "Rename Failed");
        }
    }

    private String getCaption(String path) {
        //File image = File.createTempFile(imageFileName, ".jpg", dir );
        String caption = "";
        String[] split = path.split("/");
        split = split[split.length - 1].split("_");
        caption = split[1];
        Log.d("getCaption", caption);
        return caption;
    }

    private ArrayList<String> filterGalleryByCaption(String caption, ArrayList<String> gallery) {
        Log.d("filterGalleryByCaption", "Enter");
        Date dateFrom = new Date(Long.MIN_VALUE);
        Date dateTo = new Date(Long.MAX_VALUE);
        gallery = populateGallery(dateFrom, dateTo);
        Iterator<String> itr = gallery.iterator();
        while(itr.hasNext()){
            String next = itr.next();
            if (!getCaption(next).equals(caption)) {
                itr.remove();
            }
        }
        Log.d("filterGalleryByCaption", "Exit");

        return gallery;
    }
*/



/*

    public void takePicture(View v, Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                Log.d("TryPhotoFile", "Attempt");
                photoFile = createImageFile();
                Log.d("TryPhotoFile", "Succeed");
                Log.d("TryPhotoFile", photoFile.getAbsolutePath());
            } catch (IOException ex) {
                Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "a00950540.bcit.ca.photogallery.fileprovider",
                        photoFile);
                Log.d("PhotoUri", "Succeed");

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_Caption_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", dir );
        currentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", image.getAbsolutePath());
        return image;
    }
    */
}
