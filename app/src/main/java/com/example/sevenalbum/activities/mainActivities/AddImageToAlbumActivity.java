package com.example.sevenalbum.activities.mainActivities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.adapters.ImageSelectAdapter;
import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.FindAllImagesFromDevice;
import com.example.sevenalbum.utility.ItemSelectorManagerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddImageToAlbumActivity extends AppCompatActivity implements ItemSelectorManagerInterface {
    private ImageView img_back_create_album;
    private ImageView btnTick;
    private RecyclerView rycAddAlbum;
    private List<Image> listImage;
    private ArrayList<Image> listImageSelected;
    private Intent intent;
    private String path_folder;
    private ArrayList<String> myAlbum;
    private String album_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image_to_album_activity_layout);

        intent = getIntent();
        settingData();
        mappingControls();
        event();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
        path_folder = intent.getStringExtra("path_folder");
        album_name = intent.getStringExtra("name_folder");
        myAlbum = intent.getStringArrayListExtra("list_image");
    }
    private void event() {
        img_back_create_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setViewRyc();

        btnTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAlbumAsyncTask createAlbumAsyncTask = new AddAlbumAsyncTask();
                createAlbumAsyncTask.execute();
            }
        });
    }


    private void setViewRyc() {
        listImage = FindAllImagesFromDevice.getAllImageFromGallery(this);
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this);
        imageAdapter.setListTransInterface(this);
        imageAdapter.setData(listImage);
        rycAddAlbum.setLayoutManager(new GridLayoutManager(this, 4));
        rycAddAlbum.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        img_back_create_album = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        rycAddAlbum = findViewById(R.id.rycAddAlbum);
    }

    @Override
    public void addList(Image img) {
        listImageSelected.add(img);
    }
    public void removeList(Image img) {
        listImageSelected.remove(img);
    }
    public class AddAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        ArrayList<String> list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Set<String> listAlbumImg = LocalDataManager.getAlbumSetImg(album_name);

            for (Image img :listImageSelected){
                if(listAlbumImg.add(img.getPath())) {
                    list.add(img.getPath());
                }
            }

            LocalDataManager.setAlbumListImg(album_name, listAlbumImg);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("list_result", list);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
