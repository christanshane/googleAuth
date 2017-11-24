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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginRedirect, signupBtn;
    EditText signupEmail, signupPassword, signupPassword2, signupName;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        loginRedirect = (Button)findViewById(R.id.loginRedirect);
        signupBtn = (Button)findViewById(R.id.signupBtn);
        signupEmail = (EditText)findViewById(R.id.signupEmail);
        signupPassword = (EditText)findViewById(R.id.signupPassword);
        signupPassword2 = (EditText)findViewById(R.id.signupPassword2);
        signupName = (EditText)findViewById(R.id.signupName);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginRedirect.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
    }

    private void registerUser(){
        final String name = signupName.getText().toString();
        final String email = signupEmail.getText().toString();
        final String pass = signupPassword.getText().toString();
        final String pass2 = signupPassword2.getText().toString();

        if(!pass.equals(pass2)){
            signupPassword.setError("Passwords must be the same");
            signupPassword2.setError("Passwords must be the same");
            signupPassword.requestFocus();
            return;
        }

        if(pass2.isEmpty()){
            signupPassword2.setError("Password is Required");
            signupPassword2.requestFocus();
            return;
        }

        if(name.isEmpty()){
            signupName.setError("Name is Required");
            signupName.requestFocus();
            return;
        }

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
                    saveUser(email,pass,name);
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    user.updateProfile(profileUpdates);
                    finish();
                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "You are already registered.", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void saveUser(String email, String password, final String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        HashMap<String,String> data = new HashMap<>();
        data.put("Email", email);
        data.put("UID",uid);
        data.put("Password",password);
        data.put("Name",name);

        mDatabase.push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User stored in DB", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
