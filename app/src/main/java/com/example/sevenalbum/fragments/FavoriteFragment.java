package com.example.sevenalbum.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.activities.mainActivities.SlideshowActivity;
import com.example.sevenalbum.activities.mainActivities.DataManager.LocalDataManager;
import com.example.sevenalbum.adapters.AlbumElementAdapter;

import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.FindAllImagesFromDevice;


import java.io.File;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<String> imageListPath;
    private androidx.appcompat.widget.Toolbar toolbar_favor;
    private Context context;
    private Set<String> imgListFavor;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_layout, container,false);
        context = view.getContext();
        recyclerView = view.findViewById(R.id.favor_category);
        toolbar_favor = view.findViewById(R.id.toolbar_favor);

        toolbar_favor.inflateMenu(R.menu.favorite_top_menu);
        toolbar_favor.setTitle(context.getResources().getString(R.string.favorite));

        toolbar_favor.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.album_item_search:
                        eventSearch(menuItem);
                        break;
                   case R.id.album_item_slideshow:
                       slideShowEvents();
                       break;
                }

                return true;
            }
        });

        imageListPath = LocalDataManager.getListImg();
        imgListFavor = LocalDataManager.getListSet();
        setRyc();


        return view;
    }

    private void setRyc() {

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        recyclerView.setAdapter(new AlbumElementAdapter(new ArrayList<>(imageListPath)));

    }

    private void eventSearch(@NonNull MenuItem item) {
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ArrayList<String> listImageSearch = new ArrayList<>();
                for (String image : imageListPath) {
                    if (image.toLowerCase().contains(s.toLowerCase())) {
                        listImageSearch.add(image);
                    }

                }

                if (listImageSearch.size() != 0) {
                    recyclerView.setAdapter(new AlbumElementAdapter(listImageSearch));
                    synchronized (FavoriteFragment.this) {
                        FavoriteFragment.this.notifyAll();
                    }
                } else {
                    Toast.makeText(getContext(), "Searched image not found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recyclerView.setAdapter(new AlbumElementAdapter(new ArrayList<>(imageListPath)));
                synchronized (FavoriteFragment.this) {
                    FavoriteFragment.this.notifyAll();
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        imageListPath = LocalDataManager.getListImg();
//        for(int i=0;i<imageListPath.size();i++) {
//            File file = new File(imageListPath.get(i));
//            if(!file.canRead()) {
//                imageListPath.remove(i);
//            }
//        }

        LocalDataManager.setListImgByList(imageListPath);
        recyclerView.setAdapter(new AlbumElementAdapter(new ArrayList<>(imageListPath)));
//        FavoriteFragment.MyAsyncTask myAsyncTask = new FavoriteFragment.MyAsyncTask();
//        myAsyncTask.execute();
    }

    private List<Image> getListImgFavor(List<String> imageListUri) {
        List<Image> listImageFavor = new ArrayList<>();
        List<Image> imageList = FindAllImagesFromDevice.getAllImageFromGallery(context);
        for (int i = 0; i < imageList.size(); i++) {
            for (String st: imageListUri) {
                if(imageList.get(i).getPath().equals(st)){
                    listImageFavor.add(imageList.get(i));
                }
            }
        }

        return listImageFavor;
    }
    public class MyAsyncTask extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            imageListPath = LocalDataManager.getListImg();
//            for(int i=0;i<imageListPath.size();i++) {
//                File file = new File(imageListPath.get(i));
//                if(!file.exists()|| !file.canRead()) {
//                    imageListPath.remove(i);
//                }
//            }

            LocalDataManager.setListImgByList(imageListPath);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            recyclerView.setAdapter(new AlbumElementAdapter(new ArrayList<>(imageListPath)));
        }
    }

    private void slideShowEvents() {
        Intent intent = new Intent(getView().getContext(), SlideshowActivity.class);

        ArrayList<String> list = new ArrayList<>(imageListPath.size());
        list.addAll(imageListPath);
        intent.putStringArrayListExtra("data_slide", list);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getView().getContext().startActivity(intent);
    }
}