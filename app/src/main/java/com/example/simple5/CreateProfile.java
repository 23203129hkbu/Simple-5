package com.example.simple5;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile extends AppCompatActivity {

    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button button;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,usernameRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    private static final int PICK_IMAGE =1;
    All_UserMember member;
    String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new All_UserMember();
        imageView = findViewById(R.id.iv_cp);
        etBio = findViewById(R.id.et_bio_cp);
        etEmail = findViewById(R.id.et_email_cp);
        etname = findViewById(R.id.et_name_cp);
        etProfession = findViewById(R.id.et_profession_cp);
        etWeb = findViewById(R.id.et_web_cp);
        button = findViewById(R.id.btn_cp);
        progressBar = findViewById(R.id.progressbar_cp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();


        documentReference = db.collection("user").document(currentUserId);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All Users");
        usernameRef = database.getReference("usernames");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                data != null || data.getData() != null) {
            imageUri = data.getData();


            Picasso.get().load(imageUri).into(imageView);
            //Glide.with(this).load(imageUri).into(imageView);
        }

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }
    private void uploadData() {
        final String name = etname.getText().toString();
        final String bio = etBio.getText().toString();
        final String web = etWeb.getText().toString();
        final String prof = etProfession.getText().toString();
        final String email = etEmail.getText().toString();

        try {
            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(bio) || !TextUtils.isEmpty(web) ||
                    !TextUtils.isEmpty(prof) || !TextUtils.isEmpty(email)) {

                progressBar.setVisibility(View.VISIBLE);
                if (imageUri != null) {
                    // If an image is selected, upload the image
                    final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
                    uploadTask = reference.putFile(imageUri);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            handleUploadCompletion(task, name, bio, web, prof, email);
                        }
                    });
                } else {
                    // If no image is selected, proceed without uploading an image
                    handleUploadCompletion(null, name, bio, web, prof, email);
                }
            } else {
                Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.w(TAG, "Profile creation failed", e);
        }
    }

    private void handleUploadCompletion(Task<Uri> task, String name, String bio, String web, String prof, String email) {
        String imageUrl = null;

        if (task != null && task.isSuccessful()) {
            imageUrl = task.getResult().toString();
        }

        // Prepare the profile data
        Map<String, String> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("prof", prof);
        profile.put("url", imageUrl != null ? imageUrl : ""); // Use empty string if no image
        profile.put("email", email);
        profile.put("web", web);
        profile.put("bio", bio);
        profile.put("uid", currentUserId);
        profile.put("privacy", "Public");

        // Set member data
        member.setName(name.toUpperCase());
        member.setProf(prof);
        member.setUid(currentUserId);
        member.setUrl(imageUrl != null ? imageUrl : ""); // Use empty string if no image

        // Save username
       //String key = usernameRef.push().getKey();
       //usernameRef.child(key).child("uname").setValue(name);

        // Save profile to database
        databaseReference.child(currentUserId).setValue(member);

        // Save profile to Firestore
        documentReference.set(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();

                        // Navigate to Fragment1 after a delay
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(CreateProfile.this, Fragment1.class);
                            startActivity(intent);
                            finish();
                        }, 2000);
                    }
                });
    }
}