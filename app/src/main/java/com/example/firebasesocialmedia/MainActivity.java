package com.example.firebasesocialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail,edtUsername,edtPassword;
    private Button btnSignup,btnSignin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }

        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //Transition to next activity
            transitionToSocialMediaActivity();
        }
    }

    private void signUp() {

        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,"Sign up successful",Toast.LENGTH_LONG).show();

                    FirebaseDatabase.getInstance().getReference().child("my_users").child(task.getResult().getUser().getUid()).child("username").setValue(edtUsername.getText().toString());

                    // Updating user info (see firebase documentation)
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(edtUsername.getText().toString())
                            .build();

                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Display name updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    transitionToSocialMediaActivity();
                } else {
                    Toast.makeText(MainActivity.this,"Sign up failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn() {

        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,"Sign in successful",Toast.LENGTH_LONG).show();

                    transitionToSocialMediaActivity();
                } else {
                    Toast.makeText(MainActivity.this,"Sign in  failed",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void transitionToSocialMediaActivity() {

        Intent intent = new Intent(this, SocialMediaActivity.class);
        startActivity(intent);
    }
}
