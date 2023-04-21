package com.example.sevenalbum.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.models.Image;

public class FindAllImagesFromDevice {

    public static List<Image> allImages;

    private static boolean allImagesPresent = false;
    private static boolean addNewestImagesOnly = false;

    public static List<Image> getAllImages() {
        return allImages;
    }
    public static void refreshAllImages(){
        allImagesPresent = false;
    }
    public static void updateNewImages(){
        addNewestImagesOnly = true;
    }
    public static void removeImageFromAllImages(String path) {
        Log.d("Seven-Album","FindAllImagesFromDevice -> Trying to remove "+ path);
        for(int i=0;i<allImages.size();i++) {
            if(allImages.get(i).getPath().equals(path)) {
                Log.d("Seven-Album","FindAllImagesFromDevice -> Image removed from allImages. Breaking");
                allImages.remove(i);
                break;
            }
        }
    }

    public static final List<Image> getAllImageFromGallery(Context context) {
        Log.d("Seven-Album","FindAllImagesFromDevice->FindAllImagesFromDevice()");
        Set<String> deletedList = LocalDataManager.getListDeleted();
        System.out.println("Hello " + deletedList.toString());
        if(!allImagesPresent) {
            Uri uri;
            Cursor cursor;
            int columnIndexData, thumb, dateIndex;
            List<Image> listImage = new ArrayList<>();

            String absolutePathImage = null;
            String thumbnail = null;
            Long dateTaken = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN
            };

            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;


            cursor = context.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
            columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            thumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
            dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            Calendar myCal = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MM-yyyy");

            List<String> listInternalAlbum = new ArrayList<String>();
            while (cursor.moveToNext()) {
                try {
                    absolutePathImage = cursor.getString(columnIndexData);
                    File file = new File(absolutePathImage);
                    if (!file.canRead()) {
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }
                thumbnail = cursor.getString(thumb);
                dateTaken = cursor.getLong(dateIndex);
                myCal.setTimeInMillis(dateTaken);
                String dateText = formatter.format(myCal.getTime());
                Image image = new Image();
                image.setPath(absolutePathImage);

                String[] tempData = absolutePathImage.split(File.separator);
                listInternalAlbum.add(tempData[tempData.length - 2]);

                image.setThumb(thumbnail);
                image.setDateTaken(dateText);
                if (image.getPath() == "") {
                    continue;
                }
                Log.d("Path", image.getPath());
                Log.d("Path", listImage.size() + "");
                if(addNewestImagesOnly){
                    boolean iscontained = false;
                    for(Image i : allImages){
                        if(i.getPath().equals(image.getPath())){
                            iscontained = true;
                            break;
                        }
                    }
                    if(iscontained){
                        Log.d("Seven-Album","GetAllPhotosFromGallery -> Image already in allImages. Breaking");
                        addNewestImagesOnly = false;
                        allImagesPresent = true;
                        cursor.close(); 
                        return allImages;
                    } else{
                        Log.d("Seven-Album", allImages.size() + "");
                        if(allImages.size()>1200){
                            addNewestImagesOnly = false;
                            allImagesPresent = true;
                            cursor.close();
                            return allImages;
                        }
                        allImages.add(0, image);
                    }
                } else {
                    if (deletedList.contains(image.getPath()) == false) {
                        listImage.add(image);
                    }
                }

                if(listImage.size()>128) { 
                    break;                 
                }
            }
            LocalDataManager.setInternalAlbumByList(listInternalAlbum);
            cursor.close(); 
            allImages = listImage;
            addNewestImagesOnly = false;
            allImagesPresent = true;
            return listImage;
        }
        else{
            return allImages;
        }
    }
}
