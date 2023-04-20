package com.example.sevenalbum.activities.mainActivities.DataManager;

import android.content.Context;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalDataManager {
    private static final String PREF_IMG_FAVOR = "PREF_IMG_FAVOR";

    private static final String PREF_ALBUM_LIST="PREF_ALBUM_LIST";

    private static final String PREF_INTERNAL_ALBUM_LIST="PREF_INTERNAL_ALBUM_LIST";

    private static LocalDataManager instance;

    private MySharedPreferences mySharedPreferences;

    public static void init(Context context) {
        instance = new LocalDataManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    public static LocalDataManager getInstance() {
        if(instance == null) {
            instance = new LocalDataManager();
        }
        return instance;
    }

    public static void setListImg(Set<String> listImg) {
        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(PREF_IMG_FAVOR);
        LocalDataManager.getInstance().mySharedPreferences.putStringSet(PREF_IMG_FAVOR, listImg);
    }

    public static void setAlbumListImgByList(String albumName, List<String> listImg) {
        Set<String> setListImg = new HashSet<>();

        for (String i: listImg) {
            setListImg.add(i);
        }

        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(albumName);
        LocalDataManager.getInstance().mySharedPreferences.putStringSet(albumName, setListImg);
    }

    public static void setAlbumListImg(String albumName, Set<String> listImg) {
        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(albumName);
        LocalDataManager.getInstance().mySharedPreferences.putStringSet(albumName, listImg);
    }

    public static void setAlbumByList(List<String> album){
        Set<String> setListAlbum = new HashSet<>();

        for (String i: album) {
            setListAlbum.add(i);
        }

        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(PREF_ALBUM_LIST);
        LocalDataManager.getInstance().mySharedPreferences.putStringSet(PREF_ALBUM_LIST, setListAlbum);
    }

    public static void setInternalAlbumByList(List<String> album){
        Set<String> setListAlbum = new HashSet<>();

        for (String i: album) {
            setListAlbum.add(i);
        }

        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(PREF_INTERNAL_ALBUM_LIST);
        LocalDataManager.getInstance().mySharedPreferences.putStringSet(PREF_INTERNAL_ALBUM_LIST, setListAlbum);
    }

    public static void setListImgByList(List<String> listImg){
        Set<String> setListImg = new HashSet<>();

        for (String i: listImg) {
            setListImg.add(i);
        }

        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(PREF_IMG_FAVOR);
        LocalDataManager.getInstance().mySharedPreferences.putStringSet(PREF_IMG_FAVOR, setListImg);
    }

    public static List<String> getListImg(){
        Set<String> strJsonArray = LocalDataManager.getInstance().mySharedPreferences.getStringSet(PREF_IMG_FAVOR);

        List<String> listImg = new ArrayList<>();

        for (String i: strJsonArray) {
            listImg.add(i);
        }

        return listImg;
    }

    public static List<String> getAlbumListImg(String albumName){
        Set<String> strJsonArray = LocalDataManager.getInstance().mySharedPreferences.getStringSet(albumName);

        List<String> listImg = new ArrayList<>();

        for (String i: strJsonArray) {
            listImg.add(i);
        }

        return listImg;
    }

    public static Set<String> getAlbumSetImg(String albumName){
        return LocalDataManager.getInstance().mySharedPreferences.getStringSet(albumName);
    }

    public static void deleteAlbumListImg(String albumName) {
        LocalDataManager.getInstance().mySharedPreferences.deleteListFavor(albumName);
    }

    public static List<String> getListAlbum(){
        Set<String> strJsonArray = LocalDataManager.getInstance().mySharedPreferences.getStringSet(PREF_ALBUM_LIST);

        List<String> listAlbum = new ArrayList<>();

        for (String i: strJsonArray) {
            listAlbum.add(i);
        }

        return listAlbum;
    }

    public static List<String> getListInternalAlbum(){
        Set<String> strJsonArray = LocalDataManager.getInstance().mySharedPreferences.getStringSet(PREF_INTERNAL_ALBUM_LIST);

        List<String> listAlbum = new ArrayList<>();

        for (String i: strJsonArray) {
            listAlbum.add(i);
        }

        return listAlbum;
    }

    public static Set<String> getListSet(){
        Set<String> setImg = LocalDataManager.getInstance().mySharedPreferences.getStringSet(PREF_IMG_FAVOR);
        return setImg;
    }
}