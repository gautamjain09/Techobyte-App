package com.gautamjain.techobyte.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gautamjain.techobyte.Adapter.PhotoAdapter;
import com.gautamjain.techobyte.Adapter.PostAdapter;
import com.gautamjain.techobyte.Modal.Post;
import com.gautamjain.techobyte.Modal.User;
import com.gautamjain.techobyte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.CDATASection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter photoAdapterSaves;
    private List<Post> mySavedPosts;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullname;
    private TextView username;
    private TextView bio;
    private Button editProfile;

    private ImageView myPictures;
    private ImageView savedPictures;
    private FirebaseUser firebaseUser;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        if(data.equals("none"))
        {
            profileId = firebaseUser.getUid(); //if we are clicking on our on username or id
        }
        else
        {
            profileId = data; //if we are clicking on someone else id or username
        }

        imageProfile = view.findViewById(R.id.id_profile_image);
        fullname = view.findViewById(R.id.fullname);
        username = view.findViewById(R.id.username);
        bio = view.findViewById(R.id.bio);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.followings);
        editProfile = view.findViewById(R.id.edit_profile);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);


//      <------------------------------- Recycler view My Photos ----------------------------------------->

        recyclerView = view.findViewById(R.id.id_recycler_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

//      <------------------------------- Recycler view My Saved Photos ------------------------------------->

        recyclerViewSaves = view.findViewById(R.id.id_recycler_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySavedPosts = new ArrayList<>();
        photoAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts);
        recyclerViewSaves.setAdapter(photoAdapterSaves);

        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        getMyPhotos();
        getSavedPosts();

        if(profileId.equals(firebaseUser.getUid()))
        {
            editProfile.setText("Edit Profile");
        }
        else
        {
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();

                if (btnText.equals("Edit profile"))
                {
//                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else
                {
                    if (btnText.equals("follow"))
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).setValue(true);
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                .child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(firebaseUser.getUid()).removeValue();
                    }
                }
            }
        });

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewSaves.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        return view;

    }

    private void getSavedPosts() {

        final List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    savedIds.add(dataSnapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        mySavedPosts.clear();

                        for(DataSnapshot dataSnapshot1 : snapshot1.getChildren())
                        {
                            Post post = dataSnapshot1.getValue(Post.class);

                            for(String it : savedIds)
                            {
                                if(post.getPostId().equals(it))
                                {
                                    mySavedPosts.add(post);
                                }
                            }
                        }
                        photoAdapterSaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getMyPhotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId))
                    {
                        myPhotoList.add(post);
                    }
                }

                Collections.reverse(myPhotoList); // reverse order of post as per time of post
                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void checkFollowingStatus() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(profileId).exists())
                        {
                            editProfile.setText("following");
                        }
                        else
                        {
                            editProfile.setText("follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cnt = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId))
                    {
                        cnt++;
                    }
                }
                posts.setText(String.valueOf(cnt));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getFollowersAndFollowingCount() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("following").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        following.setText("" + snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followers.setText("" + snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageUrl().equals("default"))
                {
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }

                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}