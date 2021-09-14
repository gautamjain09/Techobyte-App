package com.gautamjain.techobyte.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gautamjain.techobyte.Adapter.PostAdapter;
import com.gautamjain.techobyte.Modal.Post;
import com.gautamjain.techobyte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private List<Post> postList;
    private PostAdapter postAdapter;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPosts = view.findViewById(R.id.Recycler_view_Posts);
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);//for Showing latest Posts first
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter  = new PostAdapter(getContext(), postList);
        recyclerViewPosts.setAdapter(postAdapter);

        followingList = new ArrayList<>();
        CheckFollowingList();

        return view;
    }

    //Add all the user to the following list of current user
    private void CheckFollowingList() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    followingList.add(dataSnapshot.getKey());
                }

                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                //All others posts of following list users will now be shown in Home page Current user
                //Current user is firebaseuser
                //other user  are user
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    //Add the Post list to all the users in following list of current user
    private void readPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    for(String id : followingList)
                    {
                        if(post.getPublisher().equals(id))
                        {
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {}
        });

    }
}