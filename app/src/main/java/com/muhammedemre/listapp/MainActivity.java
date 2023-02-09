package com.muhammedemre.listapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.muhammedemre.listapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    ArrayList<person> personArrayList;
    personadapter personAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        personArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personAdapter = new personadapter(personArrayList);
        binding.recyclerView.setAdapter(personAdapter);
        getData();
    }
    public void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Person",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM person",null);

            int idIx  = cursor.getColumnIndex("id");
            int nameIx = cursor.getColumnIndex("name");
            int imgIx = cursor.getColumnIndex("image");
            while(cursor.moveToNext()) {
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                byte[] img = cursor.getBlob(imgIx);
                person human = new person(name,id,img);
                personArrayList.add(human);
            }
            personAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception err){
            System.out.println("main");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu._menu,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.addperson)
        {
            Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
            intent.putExtra("infopers","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}