package com.example.instaclone;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Feed extends AppCompatActivity {

    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    ImageView postItem,postCamera,profile;

    public static final String ANONYMOUS = "anonymous";

    private static final int SIGN_IN = 1;
    private static final int PICK_PHOTO = 2;
    String username;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private ChildEventListener childEventListener;

    //creating signUP methods list
    List<AuthUI.IdpConfig> list = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        username = ANONYMOUS;

        postItem = findViewById(R.id.postItem);
        profile = findViewById(R.id.profile);

        //getting Instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        recyclerView = findViewById(R.id.recycler_view);

        //getting database and storage references
        databaseReference = firebaseDatabase.getReference().child("posts");
        storageReference =  firebaseStorage.getReference().child("posts_storage");

        //for Recycler view
        List<Post> posts = new ArrayList<>();
        //Collections.reverse(posts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        feedAdapter  = new FeedAdapter(posts,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(feedAdapter);

        //getting the current state of the user
        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user!=null) {
                        onSignedInInitialize(user.getDisplayName());
            }
                    else {
                        onSignedOutCleanup();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(list)
                                        .build(),
                                        SIGN_IN);
                    }
            }
        };


        //for uploading image
        postItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,false);
                startActivityForResult(Intent.createChooser(intent,"PICK IMAGE"),PICK_PHOTO);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                Intent intent = new Intent(Feed.this,Profile.class);
                 startActivity(intent);
                }
         });

    }

    //handling the actions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
              if(resultCode==RESULT_OK) {

                  Toast.makeText(Feed.this, "Signed In", Toast.LENGTH_SHORT).show();
              }
            else if(requestCode==RESULT_CANCELED) {

                  Toast.makeText(Feed.this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                  finish();
            }
        }
        else if(requestCode == PICK_PHOTO) {
            if(resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                StorageReference photoreference = storageReference.child(selectedImageUri.getLastPathSegment());
                photoreference.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while(!urlTask.isSuccessful()) ;
                                    Uri downloadurl = urlTask.getResult();

                                    Post post = new Post(username,downloadurl.toString());
                                    databaseReference.push().setValue(post);
                            }
                        });
            }
        }
    }

    @Override
    protected void onResume() {
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener!=null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        feedAdapter.postsList = new ArrayList<>();
        feedAdapter.notifyDataSetChanged();

        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if(childEventListener!=null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    private void onSignedInInitialize(String username) {
        this.username = username;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (childEventListener == null) {

            childEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Post post = dataSnapshot.getValue(Post.class);

                    feedAdapter.postsList.add(post);
                    feedAdapter.notifyDataSetChanged();

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };


            databaseReference.addChildEventListener(childEventListener);
        }
    }

    private void onSignedOutCleanup() {

        username = ANONYMOUS;

        feedAdapter.postsList = new ArrayList<>();
        feedAdapter.notifyDataSetChanged();


        detachDatabaseReadListener();

    }

}



