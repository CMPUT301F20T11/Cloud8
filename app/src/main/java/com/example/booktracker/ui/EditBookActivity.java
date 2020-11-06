package com.example.booktracker.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.control.Email;
import com.example.booktracker.entities.Book;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for editing a user's book
 * @author Edlee Ducay
 */
public class EditBookActivity extends AppCompatActivity {

    private EditText titleView, authorView, descView;
    private ImageView imageView;
    private String email, isbn;
    private Book book;
    private Uri imageUri;
    private AddBookQuery addQuery;
    private ArrayList<Book> bookArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbook);

        email = getIntent().getStringExtra("USER_EMAIL");
        book = (Book) getIntent().getSerializableExtra("BOOK");
        isbn = book.getIsbn();

        titleView = findViewById(R.id.editbook_title);
        authorView = findViewById(R.id.editbook_author);
        descView = findViewById(R.id.editbook_description);
        imageView = findViewById(R.id.editbook_image);

        titleView.setText(book.getTitle());
        authorView.setText(TextUtils.join(",", book.getAuthor()));
        descView.setText(book.getDescription());
        if (book.getUri() != null) {
            imageView.setImageURI(Uri.parse(book.getUri()));
        }
        bookArray = new ArrayList<Book>();
        email = ((Email) this.getApplication()).getEmail();
        addQuery = new AddBookQuery(email);
        //===============================

        Button addBtn = findViewById(R.id.editbook_addbtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> authors = new ArrayList<String>();
                String title = titleView.getText().toString();
                String author = authorView.getText().toString();
                String desc = descView.getText().toString();

                authors.add(author);
                Book newBook = new Book(email,authors,title,isbn,desc);
                if (imageUri != null) {
                    newBook.setUri(imageUri.toString());
                }
                else {
                    newBook.setUri(book.getUri());
                }
                Toast.makeText(EditBookActivity.this, addQuery.addBook(newBook), Toast.LENGTH_LONG).show();
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

        Button cancelBtn = findViewById(R.id.editbook_cancelbtn);
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
                .start(EditBookActivity.this);
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
        }

        if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUI(ArrayList<Book> argList){
        if (argList.size() > 0){
            Book newBook = argList.get(0);//get first book of the query
            titleView.setText(newBook.getTitle());
            authorView.setText(newBook.getAuthor().get(0));
            descView.setText(newBook.getDescription());
        }else{
            Toast.makeText(EditBookActivity.this,"Book is not in GoogleBooks", Toast.LENGTH_LONG).show();
        }
    }

}
