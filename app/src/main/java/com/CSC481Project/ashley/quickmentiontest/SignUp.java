package com.CSC481Project.ashley.quickmentiontest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

import java.util.List;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;
    private FirebaseAuth mAuth;
    private UserDao userDao;
    private DaoSession daoSession;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            //start main activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        buttonRegister = (Button) findViewById(R.id.buttonSignUp);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        daoSession = ((MyApplication) getApplication()).getDaoSession();
        userDao = daoSession.getUserDao();

        List<User> joes = userDao.queryBuilder().list();
        Log.d("DaoExample", "Users in database, ID: " + joes);
    }




    private void registerUser()
    {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email))
        {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6)
        {
            Toast.makeText(this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            User user = new User();
                            user.setEmail(email);
                            userDao.insert(user);
                            Log.d("DaoExample", "Inserted new user, ID: " + user.getId());
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(SignUp.this, "Could not register. Please try again.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();

                    }
                });
    }



    @Override
    public void onClick(View v) {
        if (v == buttonRegister)
        {
            registerUser();

        }


        if (v == textViewSignIn){
            //open login activity
            finish();
            startActivity(new Intent(this, SignIn.class));
        }

    }
}
