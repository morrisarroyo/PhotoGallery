package a00950540.bcit.ca.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class GalleryHelper extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final float LOCATION_ACCURACY = 0.0001f;

    public ArrayList<String> resetGallery(ArrayList<String> photoGallery) {
        Date dateFrom = new Date(Long.MIN_VALUE);
        Date dateTo = new Date(Long.MAX_VALUE);
        photoGallery = populateGallery(dateFrom, dateTo);
        return photoGallery;
        //currentPhotoIndex = 0;
        //currentPhotoPath = photoGallery.get(0);
        //displayPhoto(currentPhotoPath);
    }

    public ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/a00950540.bcit.ca.photogallery/files/Pictures");
        ArrayList<String> photoGallery = new ArrayList<String>();
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        File[] fList = file.listFiles();


        if (fList != null) {
            for (File f : file.listFiles()) {
/*
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
*/
                photoGallery.add(f.getPath());
                // }
            }
        }
        Collections.reverse(photoGallery);
        return photoGallery;
    }

    public String renamePhoto(String caption, ArrayList<String> photoGallery
            , String currentPhotoPath, int currentPhotoIndex, Context context) {
        String renameString = "";
        File image = new File(currentPhotoPath);
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String[] split = currentPhotoPath.split("/");
        //Log.d("renamePhoto", split[split.length - 1]);
        split = split[split.length - 1].split("_");
        split[1] = caption;
        //Log.d("renamePhoto", "Caption: " + caption);
        for (int i = 1; i < split.length; ++i) {
            renameString += "_" + split[i];
        }

        File renameImage = new File(dir, renameString);
        if (image.renameTo(renameImage)) {
            photoGallery.set(currentPhotoIndex, renameImage.getAbsolutePath());
            currentPhotoPath = renameImage.getAbsolutePath();
        } else {
            //Log.d("renamePhoto", "Rename Failed");
        }
        return currentPhotoPath;
    }

    public ArrayList<String> filterGalleryByCaption(String caption, ArrayList<String> gallery) {
        //Log.d("filterGalleryByCaption", "Enter");
        //Date dateFrom = new Date(Long.MIN_VALUE);
        //Date dateTo = new Date(Long.MAX_VALUE);
        //gallery = populateGallery(dateFrom, dateTo);
        Iterator<String> itr = gallery.iterator();
        while (itr.hasNext()) {
            String next = itr.next();
            if (!getCaption(next).equals(caption)) {
                itr.remove();
            }
        }
        //Log.d("filterGalleryByCaption", "Exit");
        return gallery;
    }

    public ArrayList<String> filterGalleryByDate(Date dateFrom, Date dateTo, ArrayList<String> gallery) {
        //Log.d("filterGalleryByDate", "Enter");
        //Log.d("filterGalleryByDate", "dateFrom" + dateFrom);
        //Log.d("filterGalleryByDate", "dateTo" + dateTo);

        Iterator<String> itr = gallery.iterator();

        while (itr.hasNext()) {
            String next = itr.next();
            Date imageDate = getDateTime(getDateTimeStringFromPath(next));
            if (imageDate.compareTo(dateFrom) < 0 || imageDate.compareTo(dateTo) > 0) {
                itr.remove();
            }
        }
        ////Log.d("filterGalleryByDate", "Exit");
        return gallery;
    }

    public ArrayList<String> filterGalleryByLatitude(float latitude, ArrayList<String> gallery) {
        //Log.d("filterGalleryByLatitude", "Enter");
        //Log.d("filterGalleryByLatitude", "latitude" + latitude);

        Iterator<String> itr = gallery.iterator();
        while (itr.hasNext()) {
            String next = itr.next();
            try {
                ExifInterface exif = new ExifInterface(next);
                float[] latLong = new float[2];
                exif.getLatLong(latLong);
                float pictureLatitude = latLong[0];
                if (Math.abs(latitude - pictureLatitude) > LOCATION_ACCURACY) {
                    itr.remove();
                }
            } catch (IOException ioe)
            {
                //Log.d("filterGalleryByLatitude", "IOException");
            }
        }
        //Log.d("filterGalleryByLatitude", "Exit");
        return gallery;
    }

    public ArrayList<String> filterGalleryByLongitude(float longitude, ArrayList<String> gallery) {
        //Log.d("filterGalleryByLongitude", "Enter");
        //Log.d("filterGalleryByLongitude", "longitude" + longitude);

        Iterator<String> itr = gallery.iterator();
        while (itr.hasNext()) {
            String next = itr.next();
            try {
                ExifInterface exif = new ExifInterface(next);
                float[] latLong = new float[2];
                exif.getLatLong(latLong);
                float pictureLongitude = latLong[1];
                if (Math.abs(longitude - pictureLongitude) > LOCATION_ACCURACY) {
                    itr.remove();
                }
            } catch (IOException ioe)
            {
                //Log.d("filterGalleryByLongitude", "IOException");
            }
        }
        //Log.d("filterGalleryByLongitude", "Exit");
        return gallery;
    }

    public String getCaption(String path) {
        //File image = File.createTempFile(imageFileName, ".jpg", dir );
        String caption = "";
        String[] split = path.split("/");
        split = split[split.length - 1].split("_");
        caption = split[1];
        //Log.d("getCaption", caption);
        return caption;
    }

    public Date getDate(String dateStr) {
        Date date;
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            date = sdf.parse(dateStr);
            return date;
        }
        catch(ParseException pe) {
            return new Date();
        }
    }

    public Date getDateTime(String dateStr) {
        Date date;
        String myFormat = "yyyyMMddHHmmss"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            date = sdf.parse(dateStr);
            return date;
        }
        catch(ParseException pe) {
            return new Date();
        }
    }

    public String getDateStringFromPath(String path) {
        String dateStr = "";
        String[] split = path.split("/");
        split = split[split.length - 1].split("_");
        dateStr = split[2];
        //Log.d("getDateStringFromPath", dateStr);
        return dateStr;
    }

    public String getDateTimeStringFromPath(String path) {
        String dateStr = "";
        String[] split = path.split("/");
        split = split[split.length - 1].split("_");
        dateStr = split[2] + split[3];
        //Log.d("getDateTimeStringFromPath", dateStr);
        return dateStr;
    }

    public String takePicture(View v, Activity activity) {
        String path = "";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                //Log.d("TryPhotoFile", "Attempt");
                photoFile = createImageFile(activity);
                //Log.d("TryPhotoFile", "Succeed");
                //Log.d("TryPhotoFile", photoFile.getAbsolutePath());
            } catch (IOException ex) {
                //Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "a00950540.bcit.ca.photogallery.fileprovider",
                        photoFile);
                //Log.d("PhotoUri", "Succeed");

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                path = photoFile.getAbsolutePath();
            }
        }
        return path;
    }


    private File createImageFile(Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_Caption_" + timeStamp + "_";
        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", dir);
        //currentPhotoPath = image.getAbsolutePath();
        //Log.d("createImageFile", image.getAbsolutePath());
        return image;
    }

    /*
    public ArrayList<String> filterGalleryByLocation(float latitude, float longitude, ArrayList<String> gallery) {
        //Log.d("filterGalleryByLocation", "Enter");
        //Log.d("filterGalleryByLocation", "latitude" + latitude);
        //Log.d("filterGalleryByLocation", "longitude" + longitude);

        Location locationToCompare = new Location("");
        locationToCompare.setLatitude(latitude);
        locationToCompare.setLongitude(longitude);
        Iterator<String> itr = gallery.iterator();
        while (itr.hasNext()) {
            String next = itr.next();
            try {
                ExifInterface exif = new ExifInterface(next);
                float[] latLong = new float[2];
                exif.getLatLong(latLong);
                Location pictureLocation = new Location("");
                pictureLocation.setLatitude(latLong[0]);
                pictureLocation.setLongitude(latLong[1]);
                //Log.d("filterGalleryByLocation", "Distance: " + locationToCompare.distanceTo(pictureLocation));
                if (locationToCompare.distanceTo(pictureLocation) > LOCATION_ACCURACY) {
                    itr.remove();
                }
            } catch (IOException ioe)
            {
                //Log.d("filterGalleryByLocation", "IOException");
            }
        }
        //Log.d("filterGalleryByLocation", "Exit");
        return gallery;
    }
*/
}