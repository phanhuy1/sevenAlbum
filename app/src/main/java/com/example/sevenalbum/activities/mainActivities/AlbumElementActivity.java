package com.example.sevenalbum.activities.mainActivities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.activities.subActivities.MultiSelectAlbumElementActivity;
import com.example.sevenalbum.activities.subActivities.MultiSelectHiddenAlbumElementActivity;
import com.example.sevenalbum.adapters.AlbumElementAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AlbumElementActivity extends AppCompatActivity {
    private ArrayList<String> myAlbum;
    private String path_folder ;
    private RecyclerView ryc_album;
    private RecyclerView ryc_list_album;
    private Intent intent;
    private String album_name;
    Toolbar toolbar_item_album;
    private AlbumElementAdapter itemAlbumAdapter;
    private int isSecret;
    private int duplicateImg;
    private int isAlbum;
    private static final int REQUEST_CODE_PIC = 10;
    private static final int REQUEST_CODE_CHOOSE = 55;
    private static final int REQUEST_CODE_ADD = 56;
    private static final int REQUEST_CODE_SECRET = 57;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_element_activity_layout);
        intent = getIntent();
        mappingControls();
        setData();
        setRyc();
        events();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD) {
            ArrayList<String> resultList = data.getStringArrayListExtra("list_result");
            if(resultList !=null) {
                myAlbum.addAll(resultList);
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
            if(data != null) {
                int isMoved = data.getIntExtra("move", 0);
                if (isMoved == 1) {
                    ArrayList<String> resultList = data.getStringArrayListExtra("list_result");
                    if (resultList != null) {
                        myAlbum.remove(resultList);
                    }
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SECRET) {
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PIC) {
            String path_img = data.getStringExtra("path_img");
            if(isSecret == 1) {
                myAlbum.remove(path_img);
            }else if (duplicateImg == 2){
                myAlbum.remove(path_img);
            }
        }
    }

    private void setRyc() {
        album_name = intent.getStringExtra("name");
        ryc_list_album.setLayoutManager(new GridLayoutManager(this, 3));
        itemAlbumAdapter = new AlbumElementAdapter(myAlbum);
        ryc_list_album.setAdapter(new AlbumElementAdapter(myAlbum));
    }

    private void events() {
        // Toolbar events
        toolbar_item_album.inflateMenu(R.menu.album_element_top_menu);
        toolbar_item_album.setTitle(album_name);
        if(isAlbum == 0) {
            toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(false);
        } else
            toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(true);
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
                    case R.id.menuChoose:
                        if(isSecret == 0) {
                            Intent intent_mul = new Intent(AlbumElementActivity.this, MultiSelectAlbumElementActivity.class);
                            intent_mul.putStringArrayListExtra("data_1", myAlbum);
                            intent_mul.putExtra("name_1", album_name);
                            intent_mul.putExtra("path_folder", path_folder);
                            startActivityForResult(intent_mul, REQUEST_CODE_CHOOSE);
                        }else {
                            Intent intent_mul = new Intent(AlbumElementActivity.this, MultiSelectHiddenAlbumElementActivity.class);
                            intent_mul.putStringArrayListExtra("data_1", myAlbum);
                            intent_mul.putExtra("name_1", album_name);
                            startActivityForResult(intent_mul, REQUEST_CODE_SECRET);
                        }
                        break;
                    // case R.id.album_item_slideshow:
                    //     slideShowEvents();
                    //     break;
                    case R.id.menu_add_image:

                            Intent intent_add = new Intent(AlbumElementActivity.this, AddImageToAlbumActivity.class);
                            intent_add.putStringArrayListExtra("list_image", myAlbum);
                            intent_add.putExtra("path_folder", path_folder);
                            intent_add.putExtra("name_folder", album_name);
                            startActivityForResult(intent_add, REQUEST_CODE_ADD);

                        break;
                }

                return true;
            }
        });
        if(isSecret == 1) {
            hideMenu();
        }
    }

    private void hideMenu() {
        toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(false);
    }

    private void slideShowEvents() {
        Intent intent = new Intent(AlbumElementActivity.this, SlideshowActivity.class);
        intent.putStringArrayListExtra("data_slide", myAlbum);
        intent.putExtra("name", album_name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AlbumElementActivity.this.startActivity(intent);
    }

    private void setData() {
        myAlbum = intent.getStringArrayListExtra("data");
        path_folder = intent.getStringExtra("path_folder");
        isSecret = intent.getIntExtra("isSecret", 0);
        duplicateImg = intent.getIntExtra("duplicateImg",0);
        isAlbum = intent.getIntExtra("ok",0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);
    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            List<String> listInternalAlbum = LocalDataManager.getListInternalAlbum();
            if (listInternalAlbum.contains(album_name)) {
                return null;
            }
            List<String> albumListImg = LocalDataManager.getAlbumListImg(album_name);
            Set<String> deletedList = LocalDataManager.getListDeleted();
            if (myAlbum != null) {
                int i = 0;
                while (i < myAlbum.size()) {
                    if (!albumListImg.contains(myAlbum.get(i)) || deletedList.contains(myAlbum.get(i))) {
                        myAlbum.remove(i);
                    }
                    else {
                        i++;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ryc_list_album.setAdapter(new AlbumElementAdapter(myAlbum));
        }
    }

}