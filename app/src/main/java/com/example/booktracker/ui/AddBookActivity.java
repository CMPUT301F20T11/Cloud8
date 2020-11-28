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
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.IsbnReq;
import com.example.booktracker.control.Callback;
import com.example.booktracker.control.Email;
import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Activity for adding book's to the user's book collection
 *
 * @author Edlee Ducay
 */
public class AddBookActivity extends AppCompatActivity implements Callback,
        QueryOutputCallback {
    private Uri imageUri;
    private BookCollection bookList;
    private String email;
    private AddBookQuery addQuery;
    private ImageView imageView;
    private static int SCAN_RQ = 69;
    private IsbnReq req;
    private ArrayList<Book> bookArray;

    private EditText titleView;
    private EditText authorView;
    private EditText isbnView;
    private EditText descView;
    private EditText keywordView;

    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uid;
    private QueryOutput toast_output;
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
        keywordView = findViewById(R.id.addbook_keywords);
        imageView = findViewById(R.id.addbook_image);

        //============Ivan===============
        Button scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(view -> startActivityForResult(new Intent(view.getContext(),
                ScanActivity.class), SCAN_RQ));
        bookArray = new ArrayList<>();
        email = ((Email) this.getApplication()).getEmail();
        toast_output = new QueryOutput();
        addQuery = new AddBookQuery(email, toast_output, this);
        //===============================

        Button addBtn = findViewById(R.id.addbook_addbtn);
        addBtn.setOnClickListener(v -> {
            List<String> authors = new ArrayList<>();
            String title = titleView.getText().toString();
            String author = authorView.getText().toString();
            String isbn = isbnView.getText().toString();
            String desc = descView.getText().toString();
            String keyInput = keywordView.getText().toString();
            String[] keyArray = keyInput.split("\\s*,\\s*");
            List<String> keywords = Arrays.asList(keyArray);
            HashMap<String, String> owner = new HashMap<>();
            owner.put(email, "");
            if (isbn.length() != 13 || !isbn.matches("^[0-9]*$")) {
                isbnView.setError("ISBN must have 13 digits");
            } else {
                authors.add(author);
                Book newBook = new Book(owner, authors, title, isbn, desc, keywords);
                addQuery.loadUsername(newBook);
                upload(newBook);
            }
        });

        Button cancelBtn = findViewById(R.id.addbook_cancelbtn);
        cancelBtn.setOnClickListener(v -> finish());

        imageView.setOnClickListener(this::pickFromGallery);

        Button clearPhotoBtn = findViewById(R.id.addbook_rmPhoto_btn);
        clearPhotoBtn.setOnClickListener(v -> {
            imageView.setImageURI(null);
            imageView.setImageResource(R.drawable.ic_addbook_add_photo_foreground);
            imageUri = null;
        });
    }

    /**
     * Launches the 3rd party AndroidImageCropper activity
     * Uses a fixed aspect ratio of 1200x1200
     * Retrieved from: https://github
     * .com/mitchtabian/AndroidImageCropper-Example
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
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            // Get the uri from selected image and set into the image view
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            }
        }
        if (requestCode == SCAN_RQ) {
            if (resultCode == RESULT_OK) {
                String isbn = data.getData().toString();
                req = new IsbnReq(isbn, bookArray, AddBookActivity.this);
                req.execute();
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void executeCallback() {
        if (bookArray.size() > 0) {
            Book newBook = bookArray.get(0); // get first book of the query
            titleView.setText(newBook.getTitle());
            authorView.setText(newBook.getAuthor().get(0));
            isbnView.setText(newBook.getIsbn());
            descView.setText(newBook.getDescription());
            keywordView.setText(newBook.getKeywords());
        } else {
            Toast.makeText(AddBookActivity.this, "Book is not in GoogleBooks"
                    , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void displayQueryResult(String result) {
        Toast.makeText(AddBookActivity.this, toast_output.getOutput(),
                Toast.LENGTH_LONG).show();
        if (result.equals("Successful")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finish();
        }

    }

    /**
     * Uploads the book to the Cloud Firestore and
     * Uploads the photo to the Cloud Storage
     *
     * @param newBook
     */
    public void upload(Book newBook) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            StorageReference ref =
                    storageReference.child("images/users/" + uid + "/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(imageUri);
            // Register observers to listen for when the download is done or
            // if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    toast_output.setOutput("Upload failed");
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            toast_output.setOutput("Upload successful");
                            newBook.setUri(downloadUrl);
                            newBook.setLocalUri(imageUri.toString());
                            addQuery.loadUsername(newBook);
                            addQuery.addBook(newBook);
                            progressDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            newBook.setUri(null);
            addQuery.loadUsername(newBook);
            addQuery.addBook(newBook);
            progressDialog.dismiss();
        }
    }

}



