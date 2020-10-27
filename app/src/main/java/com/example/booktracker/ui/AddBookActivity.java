package com.example.booktracker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);

        final EditText titleView = findViewById(R.id.addbook_title);
        final EditText authorView = findViewById(R.id.addbook_author);
        final EditText isbnView = findViewById(R.id.addbook_isbn);
        final EditText descView = findViewById(R.id.addbook_description);

        Button addBtn = findViewById(R.id.addbook_addbtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title = titleView.getText().toString();
                String author = authorView.getText().toString();
                String isbn = isbnView.getText().toString();
                String desc = descView.getText().toString();

                startActivity(new Intent(v.getContext(), HomeActivity.class));

            }
        });

        Button cancelBtn = findViewById(R.id.addbook_cancelbtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), HomeActivity.class));
            }
        });

    }
}
