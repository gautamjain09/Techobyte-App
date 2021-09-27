package com.gautamjain.techobyte;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    private CircleImageView imageProfile;
    private TextView save;
    private TextView changeProfile;

    private MaterialEditText fullname;
    private MaterialEditText Username;
    private MaterialEditText Bio;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        save = findViewById(R.id.save);
        imageProfile = findViewById(R.id.image_Profile_EditProfile);
        changeProfile = findViewById(R.id.change_Profile);
        fullname = findViewById(R.id.Fullname);
        Username = findViewById(R.id.Username);
        Bio = findViewById(R.id.Bio);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    }
}