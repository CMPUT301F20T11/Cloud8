package com.example.booktracker.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.Email;
import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Activity for editing a user's book
 * @author Edlee Ducay
 */
public class EditBookActivity extends AppCompatActivity implements QueryOutputCallback {
    private EditText titleView, authorView, descView;
    private ImageView imageView;
    private String email, isbn;
    private Book book;
    private Uri imageUri;
    private AddBookQuery addQuery;
    private ArrayList<Book> bookArray;

    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uid;
    private QueryOutput toast_output;
    private String downloadUrl;
    private UpdateQuery updateQuery;
    private EditBookActivity instance = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbook);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();

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
        toast_output = new QueryOutput();
        updateQuery = new UpdateQuery();

        if (book.getUri() != null) {
            Glide.with(this).load(book.getUri()).into(imageView);
            imageUri = Uri.parse(book.getUri());
        }
        bookArray = new ArrayList<>();
        email = ((Email) this.getApplication()).getEmail();
        addQuery = new AddBookQuery(email);

        // ===================== OnClickListeners =====================

        Button addBtn = findViewById(R.id.editbook_addbtn);
        addBtn.setOnClickListener(v -> {
            List<String> authors = new ArrayList<>();
            String title = titleView.getText().toString();
            String author = authorView.getText().toString();
            String desc = descView.getText().toString();
            HashMap<String, String> owner = new HashMap<>();
            owner.put(email, "");
            authors.add(author);
            Book newBook = new Book(owner, authors, title, isbn, desc);
            addQuery.loadUsername(newBook);
            upload(newBook);

            //== Ivan: made it so that the activity automatically exits ==
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //============================================================
        });

        Button cancelBtn = findViewById(R.id.editbook_cancelbtn);
        cancelBtn.setOnClickListener(v -> finish());

        imageView.setOnClickListener(EditBookActivity.this::pickFromGallery);

        Button clearPhoto_btn = findViewById(R.id.editbook_rmPhoto_btn);
        clearPhoto_btn.setOnClickListener(v -> {
            imageView.setImageURI(null);
            imageView.setImageResource(R.drawable.ic_addbook_add_photo_foreground);
            imageUri = null;
            String localUri;
            if (book.getLocalUri() != null) {
                localUri =
                        Uri.parse(book.getLocalUri()).getLastPathSegment();
            } else {
                localUri = imageUri.getLastPathSegment();
            }

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
                .setAspectRatio(1000, 1400)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(EditBookActivity.this);
    }

    private HashMap<String, Object> createData(String title,
                                               List<String> author,
                                               String description,
                                               String imageUri,
                                               String local_image_uri) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("author", author);
        data.put("image_uri", imageUri);
        data.put("local_image_uri", local_image_uri);
        return data;
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
        if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void displayQueryResult(String result) {
        String outputResult = toast_output.getOutput();
        if (!outputResult.equals("")) {
            Toast.makeText(EditBookActivity.this, toast_output.getOutput(),
                    Toast.LENGTH_LONG).show();
        }
        if (result.equals("Successful")) {
            try {
                Thread.sleep(2000);
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

        String localUri;
        if (book.getLocalUri() != null) {
            newBook.setLocalUri(book.getLocalUri());
            localUri = Uri.parse(book.getLocalUri()).getLastPathSegment();
        } else if (imageUri != null) {
            localUri = book.getLocalUri();
        } else {
            newBook.setLocalUri(null);
            localUri = null;
        }

        if (imageUri != null) {
            StorageReference ref =
                    storageReference.child("images/users/" + uid + "/" + localUri);
            UploadTask uploadTask = ref.putFile(imageUri);
            // Register observers to listen for when the download
            // is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    toast_output.setOutput("Upload failed");
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        downloadUrl = uri.toString();
                        newBook.setUri(downloadUrl);
                        HashMap<String, Object> data =
                                createData(newBook.getTitle(),
                                        newBook.getAuthor(),
                                        newBook.getDescription(),
                                        newBook.getUri(),
                                        newBook.getLocalUri());
                        updateQuery.updateBook(newBook, instance, data,
                                toast_output);
                        progressDialog.dismiss();

                    });
                }
            });
        } else {
            newBook.setUri(null);
            HashMap<String, Object> data = createData(newBook.getTitle(),
                    newBook.getAuthor(), newBook.getDescription(), null, localUri);
            updateQuery.updateBook(newBook, instance, data, toast_output);
            progressDialog.dismiss();

        }
    }
}
