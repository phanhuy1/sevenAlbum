package com.example.sevenalbum.activities.subActivities;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.adapters.AlbumBottomSheetAdapter;
import com.example.sevenalbum.adapters.ImageSelectAdapter;
import com.example.sevenalbum.models.Album;
import com.example.sevenalbum.models.Image;
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

public class MultiSelectAlbumElementActivity extends AppCompatActivity implements ItemSelectorManagerInterface, AlbumSelectorInterface {
    private ArrayList<String> myAlbum;
    private RecyclerView ryc_album;
    private RecyclerView ryc_list_album;
    private Intent intent;
    private String album_name;
    private String path_folder;
    Toolbar toolbar_item_album;
    private BottomSheetDialog bottomSheetDialog;
    private ArrayList<Image> listImageSelected;
    private static int REQUEST_CODE_SLIDESHOW = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_element_activity_layout);
        intent = getIntent();
        setUpData();
        mappingControls();
        setData();
        setRyc();
        events();
    }

    private void setUpData() {
        listImageSelected = new ArrayList<>();
    }
    private void setRyc() {
        album_name = intent.getStringExtra("name_1");
        path_folder = intent.getStringExtra("path_folder");
        ryc_list_album.setLayoutManager(new GridLayoutManager(this, 3));
        ImageSelectAdapter imageSelectAdapter = new ImageSelectAdapter(MultiSelectAlbumElementActivity.this);
        List<Image> listImg = new ArrayList<>();
        for(int i =0 ; i< myAlbum.size();i++) {
            Image img = new Image();
            img.setThumb(myAlbum.get(i));
            img.setPath(myAlbum.get(i));
            listImg.add(img);
        }
        imageSelectAdapter.setData(listImg);
        imageSelectAdapter.setListTransInterface(this);
        ryc_list_album.setAdapter(imageSelectAdapter);
    }

    private void events() {
        toolbar_item_album.inflateMenu(R.menu.album_multi_select_top_menu);
        toolbar_item_album.setTitle(album_name);

        toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setIcon(R.drawable.ic_delete_disable);
        toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setIcon(R.drawable.ic_move_disable);
        toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setIcon(R.drawable.ic_remove_from_album_disable);
        toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setEnabled(false);
        toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setEnabled(false);
        toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setEnabled(false);

        toolbar_item_album.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_item_album.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menuMultiDelete:
                        deleteEvents();
                        break;
                    case R.id.menu_move_image:
                        moveEvent();
                        break;
                    case R.id.menu_remove_from_album:
                        removeFromAlbumEvent();
                        break;
                }

                return true;
            }
        });
    }

    private void moveEvent() {
        openBottomDialog();
    }
    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(MultiSelectAlbumElementActivity.this).inflate(R.layout.bottom_sheet_add_to_album_layout, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(MultiSelectAlbumElementActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        MultiSelectAlbumElementActivity.MyAsyncTask myAsyncTask = new MultiSelectAlbumElementActivity.MyAsyncTask();
        myAsyncTask.execute();

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

    private void removeFromAlbumEvent() {
        List<String> albumListImg = LocalDataManager.getAlbumListImg(album_name);
        listImageSelected.forEach(img -> {
            albumListImg.remove(img.getPath());
        });
        LocalDataManager.setAlbumListImgByList(album_name, albumListImg);
        setResult(RESULT_OK);
        finish();
    }

    private void setData() {
        myAlbum = intent.getStringArrayListExtra("data_1");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);

    }

    @Override
    public void addList(Image img) {
        listImageSelected.add(img);
        toggleMenuItem();
    }
    public void removeList(Image img) {
        listImageSelected.remove(img);
        toggleMenuItem();
    }

    private void toggleMenuItem() {
        if (listImageSelected != null && listImageSelected.size() > 0) {
            toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setIcon(R.drawable.ic_move);
            toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setIcon(R.drawable.ic_delete);
            toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setIcon(R.drawable.ic_remove_from_album);
            toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setEnabled(true);
            toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setEnabled(true);
            toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setEnabled(true);
        }
        else {
            toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setIcon(R.drawable.ic_move_disable);
            toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setIcon(R.drawable.ic_delete_disable);
            toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setIcon(R.drawable.ic_remove_from_album_disable);
            toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setEnabled(false);
            toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setEnabled(false);
            toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setEnabled(false);
        }
    }

    @Override
    public void add(Album album) {
        MultiSelectAlbumElementActivity.AddAlbumAsync addAlbumAsync = new MultiSelectAlbumElementActivity.AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumBottomSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {
            List<Image> listImage = FindAllImagesFromDevice.getAllImageFromGallery(MultiSelectAlbumElementActivity.this);
            listAlbum = getListAlbum(listImage);
            if(path_folder!=null) {
                for (int i = 0; i < listAlbum.size(); i++) {
                    if (path_folder.equals(listAlbum.get(i).getPathFolder())) {
                        listAlbum.remove(i);
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            albumSheetAdapter = new AlbumBottomSheetAdapter(listAlbum, MultiSelectAlbumElementActivity.this);
            albumSheetAdapter.setSubInterface(MultiSelectAlbumElementActivity.this);
            ryc_album.setAdapter(albumSheetAdapter);
            bottomSheetDialog.show();
        }
        @NonNull
        private List<Album> getListAlbum(List<Image> listImage) {
            List<Album> listAlbum = new ArrayList<>();

            List<String> albumListNames = LocalDataManager.getListAlbum();
            HashMap<String, Image> imageHashMap = new HashMap<String, Image>();
            for (int i = 0; i < listImage.size(); i++) {
                imageHashMap.put(listImage.get(i).getPath(), listImage.get(i));
            }
            for (String album : albumListNames) {
                if (album.equals(album_name)) {
                    continue;
                }
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
        ArrayList<String> list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }
        public void setAlbum(Album album) {
            this.album = album;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Set<String> listAlbumImg = new HashSet<String>(LocalDataManager.getAlbumListImg(album.getName()));
            List<String> currentListAlbumImg = LocalDataManager.getAlbumListImg(album_name);
            for (Image img :listImageSelected){
                currentListAlbumImg.remove(img.getPath());
                listAlbumImg.add(img.getPath());
            }
            LocalDataManager.setAlbumListImgByList(album_name, currentListAlbumImg);
            LocalDataManager.setAlbumListImg(album.getName(), listAlbumImg);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();
            Intent resultIntent = new Intent();

            resultIntent.putStringArrayListExtra("list_result", list);
            resultIntent.putExtra("move", 1);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
