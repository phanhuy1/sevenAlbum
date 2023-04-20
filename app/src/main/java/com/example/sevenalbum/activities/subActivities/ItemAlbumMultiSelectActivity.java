package com.example.sevenalbum.activities.subActivities;


import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.SlideShowActivity;
import com.example.sevenalbum.activities.mainActivities.data_favor.DataLocalManager;
import com.example.sevenalbum.adapters.AlbumSheetAdapter;
import com.example.sevenalbum.adapters.ImageSelectAdapter;
import com.example.sevenalbum.models.Album;
import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.GetAllPhotoFromGallery;
import com.example.sevenalbum.utility.ListTransInterface;
import com.example.sevenalbum.utility.SubInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemAlbumMultiSelectActivity extends AppCompatActivity implements ListTransInterface, SubInterface {
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
        setContentView(R.layout.activity_item_album);
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
        ImageSelectAdapter imageSelectAdapter = new ImageSelectAdapter(ItemAlbumMultiSelectActivity.this);
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
        // Toolbar events
        toolbar_item_album.inflateMenu(R.menu.menu_top_multi_album);
        toolbar_item_album.setTitle(album_name);

        toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setIcon(R.drawable.ic_delete_disable);
        toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setIcon(R.drawable.ic_move_disable);
        toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setIcon(R.drawable.ic_remove_from_album_disable);
        toolbar_item_album.getMenu().findItem(R.id.menu_move_image).setEnabled(false);
        toolbar_item_album.getMenu().findItem(R.id.menuMultiDelete).setEnabled(false);
        toolbar_item_album.getMenu().findItem(R.id.menu_remove_from_album).setEnabled(false);


        // Show back button
        toolbar_item_album.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_item_album.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Toolbar options
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
        View viewDialog = LayoutInflater.from(ItemAlbumMultiSelectActivity.this).inflate(R.layout.layout_bottom_sheet_add_to_album, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(ItemAlbumMultiSelectActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        ItemAlbumMultiSelectActivity.MyAsyncTask myAsyncTask = new ItemAlbumMultiSelectActivity.MyAsyncTask();
        myAsyncTask.execute();

    }
    private void deleteEvents() {
        for(int i=0;i<listImageSelected.size();i++) {
            Uri targetUri = Uri.parse("file://" + listImageSelected.get(i).getPath());
            File file = new File(targetUri.getPath());
            if (file.exists()){
                GetAllPhotoFromGallery.removeImageFromAllImages(targetUri.getPath());
                file.delete();
            }
            if(i==listImageSelected.size()-1) {
                setResult(RESULT_OK);
                finish();
            };
        }
    }

    private void removeFromAlbumEvent() {
        List<String> albumListImg = DataLocalManager.getAlbumListImg(album_name);
        listImageSelected.forEach(img -> {
            albumListImg.remove(img.getPath());
        });
        DataLocalManager.setAlbumListImgByList(album_name, albumListImg);
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
        ItemAlbumMultiSelectActivity.AddAlbumAsync addAlbumAsync = new ItemAlbumMultiSelectActivity.AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {
            List<Image> listImage = GetAllPhotoFromGallery.getAllImageFromGallery(ItemAlbumMultiSelectActivity.this);
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
            albumSheetAdapter = new AlbumSheetAdapter(listAlbum, ItemAlbumMultiSelectActivity.this);
            albumSheetAdapter.setSubInterface(ItemAlbumMultiSelectActivity.this);
            ryc_album.setAdapter(albumSheetAdapter);
            bottomSheetDialog.show();
        }
        @NonNull
        private List<Album> getListAlbum(List<Image> listImage) {
            List<String> ref = new ArrayList<>();
            List<Album> listAlbum = new ArrayList<>();

            List<String> albumListNames = DataLocalManager.getListAlbum();
            HashMap<String, Image> imageHashMap = new HashMap<String, Image>();
            for (int i = 0; i < listImage.size(); i++) {
                imageHashMap.put(listImage.get(i).getPath(), listImage.get(i));
//                String[] _array = listImage.get(i).getThumb().split("/");
//                String _pathFolder = listImage.get(i).getThumb().substring(0, listImage.get(i).getThumb().lastIndexOf("/"));
//                String _name = _array[_array.length - 2];
//                if (!ref.contains(_pathFolder)) {
//                    ref.add(_pathFolder);
//                    Album token = new Album(listImage.get(i), _name);
//                    token.setPathFolder(_pathFolder);
//                    token.addItem(listImage.get(i));
//                    listAlbum.add(token);
//                } else {
//                    listAlbum.get(ref.indexOf(_pathFolder)).addItem(listImage.get(i));
//                }
            }
            for (String album : albumListNames) {
                if (album.equals(album_name)) {
                    continue;
                }

                List<String> albumList = DataLocalManager.getAlbumListImg(album);
                if (albumList == null) {
                    continue;
                }

                if (albumList.size() > 0) {
                    listAlbum.add(new Album(imageHashMap.get(albumList.get(0)), album));
                    listAlbum.get(listAlbum.size() - 1).addItem(imageHashMap.get(albumList.get(0)));

                    for (int i = 1; i < albumList.size(); i++) {
                        listAlbum.get(listAlbum.size() - 1).addItem(imageHashMap.get(albumList.get(i)));
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
            //            String[] paths = new String[listImageSelected.size()];
            Set<String> listAlbumImg = new HashSet<String>(DataLocalManager.getAlbumListImg(album.getName()));
            List<String> currentListAlbumImg = DataLocalManager.getAlbumListImg(album_name);
//            int i =0;
            for (Image img :listImageSelected){
//                File imgFile = new File(img.getPath());
//                File desImgFile = new File(album.getPathFolder(),album.getName()+"_"+imgFile.getName());
//                imgFile.renameTo(desImgFile);
//                imgFile.deleteOnExit();
//                paths[i] = desImgFile.getPath();
//                i++;
                currentListAlbumImg.remove(img.getPath());
                listAlbumImg.add(img.getPath());
            }
            DataLocalManager.setAlbumListImgByList(album_name, currentListAlbumImg);
            DataLocalManager.setAlbumListImg(album.getName(), listAlbumImg);

//            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
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
