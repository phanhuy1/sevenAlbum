package com.example.sevenalbum.activities.subActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.SlideshowActivity;
import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.adapters.AlbumBottomSheetAdapter;
import com.example.sevenalbum.adapters.CategoryMultiAdapter;
import com.example.sevenalbum.models.Album;
import com.example.sevenalbum.models.Category;
import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.FileUtility;
import com.example.sevenalbum.utility.FindAllImagesFromDevice;
import com.example.sevenalbum.utility.ItemSelectorManagerInterface;
import com.example.sevenalbum.utility.AlbumSelectorInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiSelectImageActivity extends AppCompatActivity implements ItemSelectorManagerInterface, AlbumSelectorInterface {
    private RecyclerView ryc_list_album;
    private Toolbar toolbar_item_album;
    private List<Category> listImg;
    private List<Image> imageList;
    private CategoryMultiAdapter categoryMultiAdapter;
    private ArrayList<Image> listImageSelected;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    EditText edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_select_image_activity_layout);
        setUpData();
        mappingControls();
        addEvents();
    }

    private void setUpData() {
        listImageSelected = new ArrayList<>();
    }

    private void addEvents() {
        setRyc();
        eventToolBar();
    }

    private void setRyc() {
        categoryMultiAdapter = new CategoryMultiAdapter(MultiSelectImageActivity.this);
        categoryMultiAdapter.setListTransInterface(MultiSelectImageActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MultiSelectImageActivity.this, RecyclerView.VERTICAL, false);
        ryc_list_album.setLayoutManager(linearLayoutManager);

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    private void eventToolBar() {
        toolbar_item_album.inflateMenu(R.menu.select_menu);
        toolbar_item_album.setTitle("Select");

        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.menuCreateAlbum:
                        AlertDialog.Builder alert = new AlertDialog.Builder(MultiSelectImageActivity.this);
                        edittext = new EditText(MultiSelectImageActivity.this);
                        alert.setMessage("Enter album name:");
                        alert.setView(edittext);
                        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(!TextUtils.isEmpty(edittext.getText())) {
                                    MultiSelectImageActivity.CreateAlbumAsyncTask createAlbumAsyncTask = new MultiSelectImageActivity.CreateAlbumAsyncTask();
                                    createAlbumAsyncTask.execute();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Title null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alert.show();

                        break;
                    case R.id.menuMultiDelete:
                        deleteEvents();
                        break;
                   case R.id.menuSlideshow:
                       slideShowEvents();
                       break;
                    case R.id.menuAddAlbum:
                        openBottomDialog();
                        break;
                    case R.id.menuHide:
                        if(listImageSelected.size()!=0)
                        hideEvents();
                        else 
                            Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    private void hideEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultiSelectImageActivity.this);

        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to hide/show this image?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                AddSecretAsync addSecretAsync = new AddSecretAsync();
                addSecretAsync.execute();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);
    }

    private List<Category> getListCategory() {
        List<Category> categoryList = new ArrayList<>();
        int categoryCount = 0;
        imageList = FindAllImagesFromDevice.getAllImageFromGallery(MultiSelectImageActivity.this);
        Set<String> hiddenList = LocalDataManager.getListHidden();
        imageList = imageList.stream().filter(e -> !hiddenList.contains(e.getPath())).collect(Collectors.toList());

        try {
            categoryList.add(new Category(imageList.get(0).getDateTaken(),new ArrayList<>()));
            categoryList.get(categoryCount).addListImage(imageList.get(0));
            for(int i=1;i<imageList.size();i++){
                categoryList.get(categoryCount).addListImage(imageList.get(i));
            }
            return categoryList;
        } catch (Exception e){
            return null;
        }

    }
    private void deleteEvents() {
        Set<String> deletedList = LocalDataManager.getListDeleted();
        for(int i=0;i<listImageSelected.size();i++) {
            FindAllImagesFromDevice.removeImageFromAllImages(listImageSelected.get(i).getPath());
            deletedList.add(listImageSelected.get(i).getPath());
        }
        LocalDataManager.setListDeleted(deletedList);
        finish();
    }
    private void slideShowEvents() {
        Intent intent = new Intent(MultiSelectImageActivity.this, SlideshowActivity.class);
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<listImageSelected.size();i++) {
            list.add(listImageSelected.get(i).getThumb());
        }
        intent.putStringArrayListExtra("data_slide", list);
        intent.putExtra("name", "Slide Show");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(MultiSelectImageActivity.this).inflate(R.layout.bottom_sheet_add_to_album_layout, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(MultiSelectImageActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        AddSyncTask addSyncTask = new AddSyncTask();
        addSyncTask.execute();

    }

    @Override
    public void addList(Image img) {
        listImageSelected.add(img);
    }
    public void removeList(Image img) {
        listImageSelected.remove(img);
    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            listImg = getListCategory();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            categoryMultiAdapter.setData(listImg);
            ryc_list_album.setAdapter(categoryMultiAdapter);
        }
    }
    public class CreateAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String albumName = edittext.getText().toString();
            String albumPath = Environment.getExternalStorageDirectory()+ File.separator+"Pictures" + File.separator +albumName;
            File directory = new File(albumPath);
            if(!directory.exists()){
                directory.mkdirs();
                Log.e("File-no-exist",directory.getPath());
            }
            String[] paths = new String[listImageSelected.size()];
            List<String> albumListImg = new ArrayList<String>();
            int i =0;
            for (Image img :listImageSelected){
                File imgFile = new File(img.getPath());
                albumListImg.add(imgFile.getPath());

                File desImgFile = new File(albumPath,albumName+"_"+imgFile.getName());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[i] = desImgFile.getPath();
                i++;
            }
            LocalDataManager.setAlbumListImgByList(albumName, albumListImg);
            List<String> album = LocalDataManager.getListAlbum();
            album.add(albumName);
            LocalDataManager.setAlbumByList(album);
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            finish();
        }
    }
    @Override
    public void add(Album album) {
        MultiSelectImageActivity.AddAlbumAsync addAlbumAsync = new MultiSelectImageActivity.AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    public class AddSyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumBottomSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {
            List<Image> listImage = FindAllImagesFromDevice.getAllImageFromGallery(MultiSelectImageActivity.this);
            listAlbum = getListAlbum(listImage);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            albumSheetAdapter = new AlbumBottomSheetAdapter(listAlbum, MultiSelectImageActivity.this);
            albumSheetAdapter.setSubInterface(MultiSelectImageActivity.this);
            ryc_album.setAdapter(albumSheetAdapter);
            bottomSheetDialog.show();
        }
        @NonNull
        private List<Album> getListAlbum(List<Image> listImage) {
            List<String> ref = new ArrayList<>();
            List<Album> listAlbum = new ArrayList<>();

            List<String> albumListNames = LocalDataManager.getListAlbum();
            HashMap<String, Image> imageHashMap = new HashMap<String, Image>();
            for (int i = 0; i < listImage.size(); i++) {
                imageHashMap.put(listImage.get(i).getPath(), listImage.get(i));
            }
            for (String album : albumListNames) {
                List<String> albumList = LocalDataManager.getAlbumListImg(album);
                if (albumList == null) {
                    continue;
                }
                if (albumList.size() > 0) {
                    if (imageHashMap.get(albumList.get(0)) != null) {
                        listAlbum.add(new Album(imageHashMap.get(albumList.get(0)), album));
                        listAlbum.get(listAlbum.size() - 1).addItem(imageHashMap.get(albumList.get(0)));
    
                        for (int i = 1; i < albumList.size(); i++) {
                            if (imageHashMap.get(albumList.get(i)) != null) {
                                listAlbum.get(listAlbum.size() - 1).addItem(imageHashMap.get(albumList.get(i)));
                            }
                        }
                    }
                }
            }

            return listAlbum;
        }
    }
    public class AddAlbumAsync extends AsyncTask<Void, Integer, Void> {
        Album album;
        public void setAlbum(Album album) {
            this.album = album;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Set<String> listAlbumImg = new HashSet<String>(LocalDataManager.getAlbumListImg(album.getName()));
            for (Image img :listImageSelected){
                listAlbumImg.add(img.getPath());
            }
            LocalDataManager.setAlbumListImg(album.getName(), listAlbumImg);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();
            finish();
        }
    }
    public class AddSecretAsync extends AsyncTask<Void, Integer, Void> {
        private ArrayList<String> list;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String scrPath = Environment.getExternalStorageDirectory()+File.separator+".secret";
            File scrDir = new File(scrPath);
            if(!scrDir.exists()){
                Toast.makeText(MultiSelectImageActivity.this, "You haven't created secret album", Toast.LENGTH_SHORT).show();
            }
            else{
                for(int i=0;i<listImageSelected.size();i++) {
                    Image img = listImageSelected.get(i);
                    FileUtility fu = new FileUtility();
                    File imgFile = new File(img.getPath());
                    list.add(img.getPath());
                    fu.moveFile(img.getPath(), imgFile.getName(), scrPath);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent intentResult = new Intent();
            intentResult.putStringArrayListExtra("list_hide",list);
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }
}