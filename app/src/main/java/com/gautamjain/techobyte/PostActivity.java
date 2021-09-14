package com.gautamjain.techobyte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private Uri image_uri;
    private String image_url;

    private ImageView close, image_added;
    private TextView post;

    private SocialAutoCompleteTextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close_Post_Activity);
        image_added = findViewById(R.id.image_add_Post_Activity);
        post = findViewById(R.id.post_Post_Activity);
        description = findViewById(R.id.description_Post_Activity);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_image();
            }
        });

        CropImage.activity().start(PostActivity.this);

    }

    private void upload_image() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Post");
        progressDialog.show();

        if(image_uri != null)
        {
            final StorageReference imagePath = FirebaseStorage.getInstance().getReference().
                    child("Posts").child(System.currentTimeMillis() + "." + getfileExtension(image_uri));

            StorageTask upload_image = imagePath.putFile(image_uri);

            upload_image.continueWithTask(new Continuation(){
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return imagePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Uri download_uri = task.getResult();
                    image_url = download_uri.toString();

//                  <---------------------------------- Adding post Information ---------------------------->

                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = dbref.push().getKey(); // creates a unique id for dbref

                    HashMap<String, Object> mp = new HashMap<>();
                    mp.put("PostId", postId);
                    mp.put("ImageUrl",image_url);
                    mp.put("Description",description.getText().toString());
                    mp.put("Publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    dbref.child(postId).setValue(mp);

//                   <---------------------------------- Adding Hastags Information ---------------------------->

                    DatabaseReference hashtags_ref = FirebaseDatabase.getInstance().getReference().child("Hashtags");
                    List<String> hashtags = description.getHashtags();
                    if(!hashtags.isEmpty())
                    {
                        for(String tag: hashtags)
                        {
                            mp.clear();
                            mp.put("Tags",tag.toLowerCase());
                            mp.put("PostId",postId);

                            hashtags_ref.child(tag.toLowerCase()).child(postId).setValue(mp);
                        }
                    }

                    progressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getfileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image_uri = result.getUriContent();
            image_added.setImageURI(image_uri);
        }
        else
        {
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }


    // To show Available Options of hashtag when adding a new hashtag in description
    @Override
    protected void onStart() {
        super.onStart();

        ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());

        FirebaseDatabase.getInstance().getReference().child("Hashtags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    hashtagAdapter.add(new Hashtag(dataSnapshot.getKey() , (int)dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        description.setHashtagAdapter(hashtagAdapter);
    }
}