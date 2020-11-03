package com.example.booktracker.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.control.Email;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for adding book's to the user's book collection
 * @author Edlee Ducay
 */
public class AddBookActivity extends AppCompatActivity {

    private Uri imageUri;
    private BookCollection bookList;
    private String email;
    private AddBookQuery addQuery;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);

        final EditText titleView = findViewById(R.id.addbook_title);
        final EditText authorView = findViewById(R.id.addbook_author);
        final EditText isbnView = findViewById(R.id.addbook_isbn);
        final EditText descView = findViewById(R.id.addbook_description);

        imageView = findViewById(R.id.addbook_image);

        //============Ivan===============
        Button scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ScanActivity.class));
            }
        });
        email = ((Email) this.getApplication()).getEmail();

        //===============================

        Button addBtn = findViewById(R.id.addbook_addbtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> authors = new ArrayList<String>();
                String title = titleView.getText().toString();
                String author = authorView.getText().toString();
                String isbn = isbnView.getText().toString();
                String desc = descView.getText().toString();
                if (isbn.length() != 13){
                    isbnView.setError("isbn must have 13 digits");
                }
                authors.add(author);
                Book newBook = new Book(email,authors,title,isbn,desc);
                if (imageUri != null) {
                    newBook.setUri(imageUri);
                }
                addQuery = new AddBookQuery(email);
                Toast.makeText(AddBookActivity.this, addQuery.addBook(newBook), Toast.LENGTH_LONG).show();
                //====Ivan: made it so that the activity automatically exits==
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }

                finish();
                //============================================================
            }
        });

        Button cancelBtn = findViewById(R.id.addbook_cancelbtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery(v);
            }
        });

    }

    /**
     * Launches the 3rd party AndroidImageCropper activity
     * Uses a fixed aspect ratio of 1200x1200
     * Retrieved from: https://github.com/mitchtabian/AndroidImageCropper-Example
     * mitchtabian - 10/31/2020
     */
    private void pickFromGallery(View v) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1200, 1200)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(AddBookActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // Get the uri from selected image and set into the image view
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
