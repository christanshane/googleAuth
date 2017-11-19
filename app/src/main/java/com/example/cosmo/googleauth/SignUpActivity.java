package com.example.cosmo.googleauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginRedirect, signupBtn;
    EditText signupEmail, signupPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        loginRedirect = (Button)findViewById(R.id.loginRedirect);
        signupBtn = (Button)findViewById(R.id.signupBtn);
        signupEmail = (EditText)findViewById(R.id.signupEmail);
        signupPassword = (EditText)findViewById(R.id.signupPassword);
        mAuth = FirebaseAuth.getInstance();

        loginRedirect.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
    }

    private void registerUser(){
        String email = signupEmail.getText().toString();
        String pass = signupPassword.getText().toString();

        if(email.isEmpty()){
            signupEmail.setError("Email is Required");
            signupEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signupEmail.setError("Please enter a valid email");
            signupEmail.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            signupPassword.setError("Password is Required");
            signupPassword.requestFocus();
            return;
        }

        if(pass.length()<6){
            signupPassword.setError("Minimum length of password should be six(6) characters");
            signupPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "User Registered Successfully.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signupBtn:
                registerUser();
                break;
            case R.id.loginRedirect:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
