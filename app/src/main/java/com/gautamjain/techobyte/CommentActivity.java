package com.gautamjain.techobyte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gautamjain.techobyte.Adapter.CommentAdapter;
import com.gautamjain.techobyte.Modal.Comment;
import com.gautamjain.techobyte.Modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText addComment;
    private CircleImageView imageProfileComment;
    private TextView postComment;

    private String postId;
    private String authorId;

    FirebaseUser firebaseUser;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.id_toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // On clicking on top bar redirected to post of user
            }
        });

        Intent intent = getIntent();
        postId  = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        recyclerView = findViewById(R.id.id_recycler_post_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postId);
        recyclerView.setAdapter(commentAdapter);

        addComment = findViewById(R.id.comment_Text);
        postComment = findViewById(R.id.id_post_comment);
        imageProfileComment = findViewById(R.id.image_profile_comment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setting Image Profile
        setUserImage();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString()))
                {
                    Toast.makeText(CommentActivity.this, "No Comment Added", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    putComment();
                }
            }
        });

        // Showing comments in Comment Activity
        getComment();
    }

    private void getComment()
    {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void putComment() {

        HashMap<String, Object> mp =  new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        String id = ref.push().getKey();

        mp.put("id", id);
        mp.put("comments", addComment.getText().toString());
        mp.put("Publisher", firebaseUser.getUid());

        addComment.setText("");

        ref.child(id).setValue(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CommentActivity.this, "Comment Added!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//  <-------------------------------------- set Image Profile ---------------------------------------->

    private void setUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default"))
                {
                    imageProfileComment.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Picasso.get().load(user.getImageUrl()).into(imageProfileComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}