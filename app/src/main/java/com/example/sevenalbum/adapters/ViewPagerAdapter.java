package com.example.sevenalbum.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sevenalbum.fragments.AlbumFragment;
import com.example.sevenalbum.fragments.FavoriteFragment;
import com.example.sevenalbum.fragments.PhotoFragment;
import com.example.sevenalbum.fragments.HiddenPhotoFragment;
import com.example.sevenalbum.models.Image;
import com.example.sevenalbum.utility.FindAllImagesFromDevice;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<Image> data;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
        data = FindAllImagesFromDevice.getAllImageFromGallery(context);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PhotoFragment();
            case 1:
                return new AlbumFragment();
            case 2:
                return new FavoriteFragment();
            case 3:
                return new HiddenPhotoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
