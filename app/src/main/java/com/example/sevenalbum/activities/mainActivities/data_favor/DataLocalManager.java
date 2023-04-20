package com.example.sevenalbum.activities.mainActivities.data_favor;

import android.content.Context;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.sevenalbum.utility.FileUtility;

public class DataLocalManager {
    private static final String PREF_IMG_FAVOR="PREF_IMG_FAVOR";

    private static final String PREF_ALBUM_LIST="PREF_ALBUM_LIST";

    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    public static DataLocalManager getInstance() {
        if(instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setListImg(Set<String> listImg) {
        DataLocalManager.getInstance().mySharedPreferences.deleteListFavor(PREF_IMG_FAVOR);

        DataLocalManager.getInstance().mySharedPreferences.putStringSet(PREF_IMG_FAVOR, listImg);
    }

    public static void setAlbumListImgByList(String albumName, List<String> listImg) {
        Set<String> setListImg = new HashSet<>();

        for (String i: listImg) {
            setListImg.add(i);
        }

        DataLocalManager.getInstance().mySharedPreferences.deleteListFavor(albumName);

        DataLocalManager.getInstance().mySharedPreferences.putStringSet(albumName, setListImg);
    }

    public static void setAlbumListImg(String albumName, Set<String> listImg) {

        DataLocalManager.getInstance().mySharedPreferences.deleteListFavor(albumName);

        DataLocalManager.getInstance().mySharedPreferences.putStringSet(albumName, listImg);
    }

    public static void setAlbumByList(List<String> album){
        Set<String> setListAlbum = new HashSet<>();

        for (String i: album) {
            setListAlbum.add(i);
        }
        DataLocalManager.getInstance().mySharedPreferences.deleteListFavor(PREF_ALBUM_LIST);

        DataLocalManager.getInstance().mySharedPreferences.putStringSet(PREF_ALBUM_LIST, setListAlbum);

    }

    public static void setListImgByList(List<String> listImg){
        Set<String> setListImg = new HashSet<>();

        for (String i: listImg) {
            setListImg.add(i);
        }
        DataLocalManager.getInstance().mySharedPreferences.deleteListFavor(PREF_IMG_FAVOR);

        DataLocalManager.getInstance().mySharedPreferences.putStringSet(PREF_IMG_FAVOR, setListImg);

    }

    public static List<String> getListImg(){
        Set<String> strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringSet(PREF_IMG_FAVOR);

        List<String> listImg = new ArrayList<>();

        for (String i: strJsonArray) {
            listImg.add(i);
        }


        return listImg;
    }

    public static List<String> getAlbumListImg(String albumName){
        Set<String> strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringSet(albumName);

        List<String> listImg = new ArrayList<>();

        for (String i: strJsonArray) {
            listImg.add(i);
        }

        return listImg;
    }

    public static void deleteAlbumListImg(String albumName) {
        DataLocalManager.getInstance().mySharedPreferences.deleteListFavor(albumName);
    }

    public static List<String> getListAlbum(){
        Set<String> strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringSet(PREF_ALBUM_LIST);

        List<String> listAlbum = new ArrayList<>();

        for (String i: strJsonArray) {
            listAlbum.add(i);
        }


        return listAlbum;
    }

    public static Set<String> getListSet(){
        Set<String> setImg = DataLocalManager.getInstance().mySharedPreferences.getStringSet(PREF_IMG_FAVOR);
        return setImg;
    }
}