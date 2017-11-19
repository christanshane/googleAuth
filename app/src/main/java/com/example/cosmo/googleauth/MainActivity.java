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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button signupRedirect, loginBtn;
    EditText loginEmail, loginPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signupRedirect = (Button)findViewById(R.id.signupRedirect);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        loginEmail = (EditText)findViewById(R.id.loginEmail);
        loginPassword = (EditText)findViewById(R.id.loginPassword);
        mAuth = FirebaseAuth.getInstance();

        signupRedirect.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    public void userLogin(){
        String email = loginEmail.getText().toString();
        String pass = loginPassword.getText().toString();

        if(email.isEmpty()){
            loginEmail.setError("Email is Required");
            loginEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please enter a valid email");
            loginEmail.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            loginPassword.setError("Password is Required");
            loginPassword.requestFocus();
            return;
        }

        if(pass.length()<6){
            loginPassword.setError("Minimum length of password should be six(6) characters");
            loginPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.loginBtn:
                userLogin();
                break;
            case R.id.signupRedirect:
                finish();
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}
