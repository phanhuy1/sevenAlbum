package com.example.sevenalbum.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.example.sevenalbum.activities.mainActivities.AlbumElementActivity;
import com.example.sevenalbum.activities.subActivities.MultiSelectImageActivity;
import com.example.sevenalbum.utility.FindAllImagesFromDevice;
import com.example.sevenalbum.R;
import com.example.sevenalbum.models.Category;
import com.example.sevenalbum.adapters.CategoryAdapter;
import com.example.sevenalbum.models.Image;

public class PhotoFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private androidx.appcompat.widget.Toolbar toolbar_photo;
    private List<Category> listImg;
    private List<Image> imageList;
    private static int REQUEST_CODE_MULTI = 40;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_layout, container, false);
        context = view.getContext();
        recyclerView = view.findViewById(R.id.rcv_category);
        toolbar_photo = view.findViewById(R.id.toolbar_photo);
        toolBarEvents();
        setRyc();

        return view;
    }

    private void setRyc() {
        categoryAdapter = new CategoryAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        categoryAdapter.setData(getListCategory());
        recyclerView.setAdapter(categoryAdapter);

    }

    private void toolBarEvents() {
        toolbar_photo.inflateMenu(R.menu.photo_top_menu);
        toolbar_photo.setTitle(getContext().getResources().getString(R.string.photo));
        toolbar_photo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menuSearch:
                        eventSearch(item);
                        break;
                    case R.id.menuChoose:
                        Intent intent_mul = new Intent(getContext(), MultiSelectImageActivity.class);
                        startActivityForResult(intent_mul, REQUEST_CODE_MULTI);
                        break;
                }
                return true;
            }
        });
    }

    private void eventSearch(@NonNull MenuItem item) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String date = simpleDateFormat.format(calendar.getTime());
                showImageByDate(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showImageByDate(String date) {
        Toast.makeText(getContext(), date, Toast.LENGTH_LONG).show();
        List<Image> imageList = FindAllImagesFromDevice.getAllImageFromGallery(getContext());
        List<Image> listImageSearch = new ArrayList<>();

        for (Image image : imageList) {
            if (image.getDateTaken().contains(date)) {
                listImageSearch.add(image);
            }
        }

        if (listImageSearch.size() == 0) {
            Toast.makeText(getContext(), "Searched image not found", Toast.LENGTH_LONG).show();
        } else {
            ArrayList<String> listStringImage = new ArrayList<>();
            for (Image image : listImageSearch) {
                listStringImage.add(image.getPath());
            }
            Intent intent = new Intent(context, AlbumElementActivity.class);
            intent.putStringArrayListExtra("data", listStringImage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @NonNull
    private List<Category> getListCategory() {
        List<Category> categoryList = new ArrayList<>();
        int categoryCount = 0;
        imageList = FindAllImagesFromDevice.getAllImageFromGallery(getContext());

        try {
            categoryList.add(new Category(imageList.get(0).getDateTaken(), new ArrayList<>()));
            categoryList.get(categoryCount).addListImage(imageList.get(0));
            for (int i = 1; i < imageList.size(); i++) {
                categoryList.get(categoryCount).addListImage(imageList.get(i));
            }
            return categoryList;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
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
            categoryAdapter.setData(listImg);
        }
    }
}