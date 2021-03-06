package com.example.opeyemi.takeme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opeyemi.takeme.common.Common;
import com.example.opeyemi.takeme.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    EditText editPhone, editPassword;

    Button btnSingIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editPhone = (EditText) findViewById(R.id.edit_phone_signin);
        editPassword = (EditText) findViewById(R.id.edit_password_signin);
        btnSingIn = (Button) findViewById(R.id.btn_sign_in);

        TextView registerTextView = findViewById(R.id.register_textView);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        //intialize Firebase
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = mDatabase.getReference("user");


        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog mProgressDialog = new ProgressDialog(SignInActivity.this);
                mProgressDialog.setMessage("please wait...");
                mProgressDialog.show();

                table_user.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if user exist in the database
                        if (dataSnapshot.child(editPhone.getText().toString()).exists()) {

                            mProgressDialog.dismiss();

                            //Get User Information
                            User user = dataSnapshot.child(editPhone.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(editPassword.getText().toString())) {
                                Toast.makeText(SignInActivity.this, "Signed in successfully !", Toast.LENGTH_SHORT).show();
                                Common.currentUser = user;
                                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(homeIntent);
                                SignInActivity.this.finish();
                            } else {
                                Toast.makeText(SignInActivity.this, "wrong username or password", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                            {
                                Toast.makeText(SignInActivity.this,
                                        "user do not exist", Toast.LENGTH_LONG ).show();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        if(Common.currentUser != null){
            startActivity(new Intent (SignInActivity.this, MainActivity.class));
        }
    }
}
