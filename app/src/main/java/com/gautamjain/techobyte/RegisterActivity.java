package com.gautamjain.techobyte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText id_username, id_name, id_password, id_email;
    private Button id_register;
    private TextView id_login;

    private FirebaseAuth auth;
    private DatabaseReference dbref;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id_username = findViewById(R.id.id_Username_register);
        id_email = findViewById(R.id.id_email_register);
        id_name = findViewById(R.id.id_name_register);
        id_password = findViewById(R.id.id_passoword_register);

        id_login = findViewById(R.id.id_login_register_activity);
        id_register = findViewById(R.id.id_registration_register);

        auth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference();

        progressDialog =  new ProgressDialog(this);

        id_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        id_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_username = id_username.getText().toString();
                String text_name = id_name.getText().toString();
                String text_password = id_password.getText().toString();
                String text_email = id_email.getText().toString();

                if(TextUtils.isEmpty(text_name) || TextUtils.isEmpty(text_email) || TextUtils.isEmpty(text_password)
                        || TextUtils.isEmpty(text_username))
                {
                    Toast.makeText(RegisterActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                }
                else if(text_password.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Register_user(text_username, text_name, text_email, text_password);
                }
            }
        });
    }

    private void Register_user(String username, String name, String email, String password) {

        progressDialog.setMessage("Registering");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String , Object> mp = new HashMap<>();
                mp.put("name",name);
                mp.put("email", email);
                mp.put("username",username);
                mp.put("id", auth.getCurrentUser().getUid());
                mp.put("bio","");
                mp.put("imageUrl","default");

                dbref.child("Users").child(auth.getCurrentUser().getUid()).setValue(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Welcome to Techobyte!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}