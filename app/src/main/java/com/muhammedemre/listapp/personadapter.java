package com.muhammedemre.listapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muhammedemre.listapp.databinding.RecyclerowBinding;

import java.util.ArrayList;

public class personadapter extends RecyclerView.Adapter<personadapter.personHolder> {

    ArrayList<person> personArrayList;

    public personadapter(ArrayList<person> personArrayList) {
        this.personArrayList = personArrayList;
    }

    @NonNull
    @Override
    public personHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerowBinding recyclerowBinding = RecyclerowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new personHolder(recyclerowBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull personHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.recyclertext.setText(personArrayList.get(position).name);
        Bitmap bmp = BitmapFactory.decodeByteArray(personArrayList.get(position).imgbyte, 0, personArrayList.get(position).imgbyte.length);
        holder.binding.imageView8.setImageBitmap(bmp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailActivity.class);
                intent.putExtra("infopers","old");
                intent.putExtra("id",personArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return personArrayList.size();
    }

    public class personHolder extends RecyclerView.ViewHolder {

        RecyclerowBinding binding;

        public personHolder(RecyclerowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
