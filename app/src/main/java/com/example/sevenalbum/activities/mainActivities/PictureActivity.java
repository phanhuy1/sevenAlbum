package com.example.sevenalbum.activities.mainActivities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.ParcelFileDescriptor;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.adapters.AlbumBottomSheetAdapter;
import com.example.sevenalbum.adapters.SlideAdapter;
import com.example.sevenalbum.models.Album;
import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.FileUtility;
import com.example.sevenalbum.utility.FindAllImagesFromDevice;
import com.example.sevenalbum.utility.ImageInterface;
import com.example.sevenalbum.utility.AlbumSelectorInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;


public class PictureActivity extends AppCompatActivity implements ImageInterface, AlbumSelectorInterface {
    private ViewPager viewPager_picture;
    private Toolbar toolbar_picture;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frame_viewPager;
    private ArrayList<String> imageListThumb;
    private ArrayList<String> imageListPath;
    private Intent intent;
    private int pos;
    private SlideAdapter slideImageAdapter;
    private ImageInterface activityPicture;
    private String imgPath;
    private String imageName;
    private String thumb;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    public static Set<String> imageListFavor = LocalDataManager.getListSet();
    @Override
    protected void onResume() {
        super.onResume();
        imageListFavor = LocalDataManager.getListSet();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_layout);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mappingControls();
        events();
    }

    private void events() {
        setDataIntent();
        setUpToolBar();
        setUpSilder();
        bottomNavigationViewEvents();
    }

    private void bottomNavigationViewEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Uri targetUri = Uri.parse("file://" + thumb);
                switch (item.getItemId()) {

                    case R.id.sharePic:

                        if(thumb.contains("gif")){
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/*");
                            share.putExtra(Intent.EXTRA_STREAM, targetUri);
                            startActivity( Intent.createChooser(share, "Share this image to your friends!") );
                        }
                        else {
                            Drawable mDrawable = Drawable.createFromPath(imgPath);
                            Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);
                            thumb = thumb.replaceAll(" ", "");

                            Uri uri = Uri.parse(path);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        }

                        break;

                    case R.id.editPic:
                        Intent editIntent = new Intent(PictureActivity.this, DsPhotoEditorActivity.class);

                        if(imgPath.contains("gif")){
                            Toast.makeText(PictureActivity.this,"Cannot edit GIF images",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            editIntent.setData(Uri.fromFile(new File(imgPath)));
                            editIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Edited Photo");
                            editIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
                            editIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
                            startActivity(editIntent);
                        }
                        break;

                    case R.id.heartPic:

                        if(!imageListFavor.add(imgPath)){
                            imageListFavor.remove(imgPath);
                        }

                        LocalDataManager.setListImg(imageListFavor);
                        if(!check(imgPath)){
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_heart);
                            Toast.makeText(PictureActivity.this, "Removed from your favorite list!" , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_heart_fill);
                            Toast.makeText(PictureActivity.this, "Added to your favorite list!" , Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.deletePic:

                        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);

                        builder.setTitle("Confirmation");
                        builder.setMessage("Do you really want to delete this image?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(targetUri.getPath());

                                if (file.exists()) {
                                    Set<String> deletedList = LocalDataManager.getListDeleted();
                                    deletedList.add(targetUri.getPath());
                                    LocalDataManager.setListDeleted(deletedList);
                                    FindAllImagesFromDevice.removeImageFromAllImages(targetUri.getPath());
                                    Toast.makeText(PictureActivity.this, "Delete successfully!", Toast.LENGTH_SHORT).show();
                                }
                                finish();
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

                        break;
                }
                return true;
            }

        });
    }

    private void showNavigation(boolean flag) {
        if (!flag) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
            toolbar_picture.setVisibility(View.INVISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            toolbar_picture.setVisibility(View.VISIBLE);
        }
    }

    private void setUpToolBar() {
        toolbar_picture.inflateMenu(R.menu.picture_top_menu);

        toolbar_picture.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_picture.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar_picture.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
//                    case R.id.menuInfo:
//                        Uri targetUri = Uri.parse("file://" + thumb);
//                        if (targetUri != null) {
//                            showExif(targetUri);
//                        }
//                        break;
                    case R.id.menuAddAlbum:
                        openBottomDialog();
                        break;
                    case R.id.menuAddSecret:
                        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);

                        builder.setTitle("Confirmation");
                        builder.setMessage("Do you want to hide/show this image?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                File scrDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), ".secret");
                                String scrPath = scrDir.getPath();
                                if(!scrDir.exists()){
                                    Toast.makeText(PictureActivity.this, "You haven't created secret album", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    FileUtility fu = new FileUtility();
                                    File img = new File(imgPath);
                                    System.out.println("PicAct imgPath: "+ img.getPath());
                                    System.out.println("PicAct imgName: " + img.getName());
                                    if(!(scrPath+File.separator+img.getName()).equals(imgPath)){
                                        fu.moveFile(imgPath,img.getName(),scrPath);
                                        Toast.makeText(PictureActivity.this, "Your image is hidden", Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Restore");
                                        String outputPath = folder.getPath();
                                        File imgFile = new File(img.getPath());
                                        File desImgFile = new File(outputPath,imgFile.getName());
                                        if(!folder.exists()) {
                                            folder.mkdir();
                                        }
                                        imgFile.renameTo(desImgFile);
                                        imgFile.deleteOnExit();
                                        desImgFile.getPath();
                                        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{outputPath+File.separator+desImgFile.getName()}, null, null);
                                    }
                                }
                                Intent intentResult = new Intent();
                                intentResult.putExtra("path_img", imgPath);
                                setResult(RESULT_OK, intentResult);
                                finish();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                        break;
                    case R.id.setWallpaper:
                        Uri uri_wallpaper = Uri.parse("file://" + thumb);
                        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(uri_wallpaper, "image/*");
                        intent.putExtra("mimeType", "image/*");
                        startActivity(Intent.createChooser(intent, "Set as:"));
                }

                return true;
            }
        });
    }

    private void showExif(Uri photoUri) {
        if (photoUri != null) {

            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                ExifInterface exifInterface = new ExifInterface(fileDescriptor);

                BottomSheetDialog infoDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View infoDialogView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.info_layout,
                                (LinearLayout) findViewById(R.id.infoContainer),
                                false
                        );
                TextView txtInfoProducer = (TextView) infoDialogView.findViewById(R.id.txtInfoProducer);
                TextView txtInfoSize = (TextView) infoDialogView.findViewById(R.id.txtInfoSize);
                TextView txtInfoModel = (TextView) infoDialogView.findViewById(R.id.txtInfoModel);
                TextView txtInfoFlash = (TextView) infoDialogView.findViewById(R.id.txtInfoFlash);
                TextView txtInfoFocalLength = (TextView) infoDialogView.findViewById(R.id.txtInfoFocalLength);
                TextView txtInfoAuthor = (TextView) infoDialogView.findViewById(R.id.txtInfoAuthor);
                TextView txtInfoTime = (TextView) infoDialogView.findViewById(R.id.txtInfoTime);
                TextView txtInfoName = (TextView) infoDialogView.findViewById(R.id.txtInfoName);

                txtInfoName.setText(imageName);
                txtInfoProducer.setText(exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                txtInfoSize.setText(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + "x" + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
                txtInfoModel.setText(exifInterface.getAttribute(ExifInterface.TAG_MODEL));
                txtInfoFlash.setText(exifInterface.getAttribute(ExifInterface.TAG_FLASH));
                txtInfoFocalLength.setText(exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
                txtInfoAuthor.setText(exifInterface.getAttribute(ExifInterface.TAG_ARTIST));
                txtInfoTime.setText(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));

                infoDialog.setContentView(infoDialogView);
                infoDialog.show();

                parcelFileDescriptor.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    }

    ;



    private void setUpSilder() {

        slideImageAdapter = new SlideAdapter();
        slideImageAdapter.setData(imageListThumb, imageListPath);
        slideImageAdapter.setContext(getApplicationContext());
        slideImageAdapter.setPictureInterface(activityPicture);
        viewPager_picture.setAdapter(slideImageAdapter);
        viewPager_picture.setCurrentItem(pos);

        viewPager_picture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                thumb = imageListThumb.get(position);
                imgPath = imageListPath.get(position);
                setTitleToolbar(thumb.substring(thumb.lastIndexOf('/') + 1));
                if(!check(imgPath)){
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_heart);
                }
                else{
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_heart_fill);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setDataIntent() {
        intent = getIntent();
        imageListPath = intent.getStringArrayListExtra("data_list_path");
        imageListThumb = intent.getStringArrayListExtra("data_list_thumb");
        pos = intent.getIntExtra("pos", 0);
        activityPicture = this;

    }

    private void mappingControls() {
        viewPager_picture = findViewById(R.id.viewPager_picture);
        bottomNavigationView = findViewById(R.id.bottom_picture);



        toolbar_picture = findViewById(R.id.toolbar_picture);
        frame_viewPager = findViewById(R.id.frame_viewPager);
    }

    public Boolean check(String  Path){
        for (String img: imageListFavor) {
            if(img.equals(Path)){
                return true;
            }
        }
        return false;
    }

    public void setTitleToolbar(String imageName) {
        this.imageName = imageName;
        toolbar_picture.setTitle(imageName);

    }
    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(PictureActivity.this).inflate(R.layout.bottom_sheet_add_to_album_layout, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(PictureActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    @Override
    public void actionShow(boolean flag) {
        showNavigation(flag);
    }

    @Override
    public void add(Album album) {
        AddAlbumAsync addAlbumAsync = new AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumBottomSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {
            List<Image> listImage = FindAllImagesFromDevice.getAllImageFromGallery(PictureActivity.this);
            listAlbum = getListAlbum(listImage);
            String path_folder = imgPath.substring(0, imgPath.lastIndexOf("/"));
            for(int i =0;i<listAlbum.size();i++) {
                if(path_folder.equals(listAlbum.get(i).getPathFolder())) {
                    listAlbum.remove(i);
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            albumSheetAdapter = new AlbumBottomSheetAdapter(listAlbum, PictureActivity.this);
            albumSheetAdapter.setSubInterface(PictureActivity.this);
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
            List<String> listAlbumImg = LocalDataManager.getAlbumListImg(album.getName());
            listAlbumImg.add(imgPath);
            LocalDataManager.setAlbumListImgByList(album.getName(), listAlbumImg);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();
        }
    }
}