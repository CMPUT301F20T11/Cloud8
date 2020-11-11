package com.example.booktracker.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.boundary.IsbnReq;
import com.example.booktracker.control.Email;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity for adding book's to the user's book collection
 * @author Edlee Ducay
 */
public class AddBookActivity extends AppCompatActivity {

    private Uri imageUri;
    private BookCollection bookList;
    private String email;
    private Book book;
    private AddBookQuery addQuery;
    private ImageView imageView;
    private static int SCAN_RQ = 69;
    private IsbnReq req;
    private ArrayList<Book> bookArray;

    EditText titleView;
    EditText authorView;
    EditText isbnView;
    EditText descView;

    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uid;
    private String toast_output;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();

        titleView = findViewById(R.id.addbook_title);
        authorView = findViewById(R.id.addbook_author);
        isbnView = findViewById(R.id.addbook_isbn);
        descView = findViewById(R.id.addbook_description);
        imageView = findViewById(R.id.addbook_image);
        //============Ivan===============

        Button scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), ScanActivity.class),SCAN_RQ);
            }
        });
        bookArray = new ArrayList<Book>();
        email = ((Email) this.getApplication()).getEmail();
        addQuery = new AddBookQuery(email);
        //===============================

        Button addBtn = findViewById(R.id.addbook_addbtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<String> authors = new ArrayList<String>();
                String title = titleView.getText().toString();
                String author = authorView.getText().toString();
                String isbn = isbnView.getText().toString();
                String desc = descView.getText().toString();
                if (isbn.length() != 13 || !isbn.matches("^[0-9]*$")){
                    isbnView.setError("isbn must have 13 digits");
                }else{
                    authors.add(author);
                    Book newBook = new Book(email,authors,title,isbn,desc);
                    upload(newBook);

                    //====Ivan: made it so that the activity automatically exits==
                    try{
                        Thread.sleep(2000);
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }

                    finish();
                    //============================================================

                }
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

        Button clearPhotoBtn = findViewById(R.id.addbook_rmPhoto_btn);
        clearPhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageView.setImageURI(null);
                imageView.setImageResource(R.drawable.ic_stock_book_photo_foreground);
                imageUri = null;
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
        }
        if (requestCode == SCAN_RQ){
            if (resultCode == RESULT_OK){
                String isbn = data.getData().toString();
                req = new IsbnReq(isbn,bookArray,AddBookActivity.this);
                req.execute();
            }
        }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    public void updateUI(ArrayList<Book> argList){
        if (argList.size() > 0){
            Book newBook = argList.get(0);//get first book of the query
            titleView.setText(newBook.getTitle());
            authorView.setText(newBook.getAuthor().get(0));
            isbnView.setText(newBook.getIsbn());
            descView.setText(newBook.getDescription());
        }else{
            Toast.makeText(AddBookActivity.this,"Book is not in GoogleBooks", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Uploads the book to the Cloud Firestore and
     * Uploads the photo to the Cloud Storage
     * @param newBook
     */
    public void upload(Book newBook) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            StorageReference ref = storageReference.child("images/users/" + uid + "/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(imageUri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    toast_output = "Upload failed";
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            toast_output = "Upload successful";
                            newBook.setUri(downloadUrl);
                            newBook.setLocalUri(imageUri.toString());
                            addQuery.addBook(newBook);
                            Toast.makeText(AddBookActivity.this, "Book Added Successfully" , Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    });
                }
            });
        } else {
            newBook.setUri(null);
            addQuery.addBook(newBook);
            Toast.makeText(AddBookActivity.this, "Book Added Successfully", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }




    }

}



