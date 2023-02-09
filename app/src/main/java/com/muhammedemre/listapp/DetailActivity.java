package com.muhammedemre.listapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.muhammedemre.listapp.databinding.ActivityDetailBinding;
import com.muhammedemre.listapp.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;// galeriye gitmek için
    ActivityResultLauncher<String> permissionLauncher;// izin istemek için
    Bitmap selectedImage;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        binding.button.setVisibility(View.VISIBLE);
        database = this.openOrCreateDatabase("Person",MODE_PRIVATE,null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("infopers");
        System.out.println(info);
        switch (info){
            case "new":
                binding.enterName.setText("");
                binding.enterComment.setText("");
                binding.enterPhone.setText("");
                binding.enterSurname.setText("");
                binding.imageView2.setImageResource(R.drawable.selectimage);
                binding.button.setVisibility(View.VISIBLE);
                break;
            case "old":
                binding.button.setVisibility(View.INVISIBLE);
                //created person
                int personId = intent.getIntExtra("id",0);

                try {
                    Cursor cursor = database.rawQuery("SELECT * FROM person WHERE id = ?",new String[]{String.valueOf(personId)});
                    int nameIx = cursor.getColumnIndex("name");
                    int surnameIx = cursor.getColumnIndex("surname");
                    int phoneIx = cursor.getColumnIndex("phone");
                    int detailIx = cursor.getColumnIndex("detail");
                    int imageIx = cursor.getColumnIndex("image");

                    while(cursor.moveToNext()){
                        Bitmap bimage = BitmapFactory.decodeByteArray(cursor.getBlob(imageIx), 0, cursor.getBlob(imageIx).length);
                        binding.imageView2.setImageBitmap(bimage);
                        binding.enterSurname.setText(cursor.getString(surnameIx));
                        binding.enterPhone.setText(cursor.getString(phoneIx));
                        binding.enterComment.setText(cursor.getString(detailIx));
                        binding.enterName.setText(cursor.getString(nameIx));
                    }

                }catch (Exception err){
                    System.out.println("get");
                }
                break;

        }
        /*if(info == "new"){
            //new person
            binding.enterName.setText("");
            binding.enterComment.setText("");
            binding.enterPhone.setText("");
            binding.enterSurname.setText("");
            binding.imageView2.setImageResource(R.drawable.selectimage);
            binding.button.setVisibility(View.VISIBLE);
        }else if (info=="old"){
            binding.button.setVisibility(View.INVISIBLE);
            //created person
            int personId = intent.getIntExtra("id",0);

            try {
                Cursor cursor = database.rawQuery("SELECT * FROM person WHERE id = ?",new String[]{String.valueOf(personId)});
                int nameIx = cursor.getColumnIndex("name");
                int surnameIx = cursor.getColumnIndex("surname");
                int phoneIx = cursor.getColumnIndex("phone");
                int detailIx = cursor.getColumnIndex("detail");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    Bitmap bimage = BitmapFactory.decodeByteArray(cursor.getBlob(imageIx), 0, cursor.getBlob(imageIx).length);
                    binding.imageView2.setImageBitmap(bimage);
                    binding.enterSurname.setText(cursor.getString(surnameIx));
                    binding.enterPhone.setText(cursor.getString(phoneIx));
                    binding.enterComment.setText(cursor.getString(detailIx));
                    binding.enterName.setText(cursor.getString(nameIx));
                }

            }catch (Exception err){
                System.out.println("get");
            }
        }*/
    }

    public void save(View view) {
        String name = binding.enterName.getText().toString();
        String surname = binding.enterSurname.getText().toString();
        String phonenummber = binding.enterPhone.getText().toString();
        String detail = binding.enterComment.getText().toString();
        Bitmap smallimg = smallimage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallimg.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] imgbyte = outputStream.toByteArray();
        try {


            database.execSQL("CREATE TABLE IF NOT EXISTS person (id INTEGER PRIMARY KEY ,name VARCHAR,surname VARCHAR,phone VARCHAR,detail VARCHAR , image BLOB)");
            String sqlString = "INSERT INTO person (name,surname,phone,detail,image) VALUES (?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);

            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,surname);
            sqLiteStatement.bindString(3,phonenummber);
            sqLiteStatement.bindString(4,detail);
            sqLiteStatement.bindBlob(5,imgbyte);
            sqLiteStatement.execute();

            Intent intent = new Intent(DetailActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//bundan önceki bütün aktiviteleri kapattı ilk başa finish(); yazarak da yabılabilir
            startActivity(intent);

        }catch (Exception err){
            System.out.println("detail");
        }


    }
    public Bitmap smallimage(Bitmap image ,int maxsize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapOran = (float) width/ (float) height;
        if(bitmapOran>1){
            //yatay
            width = maxsize;
            height = (int)(maxsize/bitmapOran);
        }else{
            //dikey
            height = maxsize;
            width = (int)(maxsize*bitmapOran);
        }

        return image.createScaledBitmap(image,width,height,true);
    }

    public void selectimage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){//izin verilmemişse
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"galeri için izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izin isteme
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else{
                //daha önce izin verilmediyse  izin isteme
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            Intent intentGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentGalery);
        }
    }

    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent resultFromData = result.getData();// intent deki put extra ve get olayındaki gibi fotoyu çekiyoz
                    if(resultFromData != null){
                        Uri image = resultFromData.getData();//dosyanın nerde olduğu

                        try {
                            if(Build.VERSION.SDK_INT>27){
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),image);
                                 selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView2.setImageBitmap(selectedImage);
                            }
                            else{
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                                binding.imageView2.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            System.out.println("hata");
                        }
                    }
                }
                else{

                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //izin verilirse galeriyi açma
                    Intent intentGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentGalery);
                }else{
                    Toast.makeText(DetailActivity.this,"İzin gerekli",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}