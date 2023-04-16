package com.example.sevenalbum.activities.mainActivities;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.data_favor.DataLocalManager;
import com.example.sevenalbum.adapters.ImageSelectAdapter;
import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.GetAllPhotoFromGallery;
import com.example.sevenalbum.utility.ListTransInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateAlbumActivity extends AppCompatActivity implements ListTransInterface {
    private ImageView img_back_create_album;
    private ImageView btnTick;
    private EditText edtTitleAlbum;
    private RecyclerView rycAddAlbum;
    private List<Image> listImage;
    private ArrayList<Image> listImageSelected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        settingData();
        mappingControls();
        event();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
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
                if(!TextUtils.isEmpty(edtTitleAlbum.getText())) {
                    if(edtTitleAlbum.getText().toString().contains("#")) {
                        Toast.makeText(getApplicationContext(), "Không được chứa kí tự #", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        CreateAlbumAsyncTask createAlbumAsyncTask = new CreateAlbumAsyncTask();
                        createAlbumAsyncTask.execute();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Title null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void action() {
        Intent intent = new Intent(this, SlideShowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }*/

    private void setViewRyc() {
        listImage = GetAllPhotoFromGallery.getAllImageFromGallery(this);
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this);
        imageAdapter.setListTransInterface(this);
        imageAdapter.setData(listImage);
        rycAddAlbum.setLayoutManager(new GridLayoutManager(this, 4));
        rycAddAlbum.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        img_back_create_album = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        edtTitleAlbum = findViewById(R.id.edtTitleAlbum);
        rycAddAlbum = findViewById(R.id.rycAddAlbum);
    }

    @Override
    public void addList(Image img) {
        listImageSelected.add(img);
    }
    public void removeList(Image img) {
        listImageSelected.remove(img);
    }
    public class CreateAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String albumName = edtTitleAlbum.getText().toString();
            String albumPath = Environment.getExternalStorageDirectory()+File.separator+"Pictures" + File.separator +albumName;
            File directory = new File(albumPath);
            if(!directory.exists()){
                directory.mkdirs();
                Log.e("File-no-exist",directory.getPath());
            }
            String[] paths = new String[listImageSelected.size()];
            int i =0;
            Set<String> imageListFavor = DataLocalManager.getListSet();
            for (Image img :listImageSelected){
                File imgFile = new File(img.getPath());
                File desImgFile = new File(albumPath,albumName+"_"+imgFile.getName());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[i] = desImgFile.getPath();
                i++;
                for (String imgFavor: imageListFavor){
                    if(imgFavor.equals(imgFile.getPath())){
                        imageListFavor.remove(imgFile.getPath());
                        imageListFavor.add(desImgFile.getPath());
                        break;
                    }
                }
            }
            DataLocalManager.setListImg(imageListFavor);
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
