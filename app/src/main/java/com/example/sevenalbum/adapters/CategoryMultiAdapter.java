package com.example.sevenalbum.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sevenalbum.R;
import com.example.sevenalbum.models.Category;
import com.example.sevenalbum.utility.ItemSelectorManagerInterface;

import java.util.List;

public class CategoryMultiAdapter extends RecyclerView.Adapter<CategoryMultiAdapter.CategoryViewHolder>{
    private Context context;
    private List<Category> listCategory;
    private ItemSelectorManagerInterface listTransInterface;

    public CategoryMultiAdapter(Context context) {
        this.context = context;
    }
    public void setListTransInterface(ItemSelectorManagerInterface listTransInterface) {
        this.listTransInterface = listTransInterface;
    }
    public void setData(List<Category> listCategory){
        this.listCategory = listCategory;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_element_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCategory.get(position);
        if (category == null)
            return;

//        holder.tvNameCategory.setText(category.getNameCategory());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        holder.rcvPictures.setLayoutManager(gridLayoutManager);

        ImageSelectAdapter imageSelectAdapter = new ImageSelectAdapter(context.getApplicationContext());
        imageSelectAdapter.setListTransInterface(listTransInterface);
        imageSelectAdapter.setData(category.getListImage());
        imageSelectAdapter.setListCategory(listCategory);
        holder.rcvPictures.setAdapter(imageSelectAdapter);


    }

    @Override
    public int getItemCount() {
        if (listCategory != null){
            return listCategory.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
//        private TextView tvNameCategory;
        private RecyclerView rcvPictures;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            rcvPictures = itemView.findViewById(R.id.rcvPictures);
        }
    }
}
