package com.example.instagram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Menu menu;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private EditText mDescription;
    String miUrlOk = "";
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserRef;
    private StorageTask mUploadTask;

    private DatabaseReference mHashTagRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mDescription=findViewById(R.id.edit_description);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mHashTagRef=FirebaseDatabase.getInstance().getReference("hashtaging");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ProfileActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

   private void uploadFile() {
       final ProgressDialog pd = new ProgressDialog(this);
       pd.setMessage("Posting");
       pd.show();
       if (mImageUri != null) {
          final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                   + "." + getFileExtension(mImageUri));

           mUploadTask = fileReference.putFile(mImageUri);

           mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
               @Override
               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
               {
                   if (!task.isSuccessful())
                   {
                       throw task.getException();
                   }
                   return fileReference.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull final Task<Uri> task) {
                   if (task.isSuccessful()) {

                       final FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser();
                       mUserRef=FirebaseDatabase.getInstance().getReference("User");
                       mUserRef.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if(dataSnapshot.exists()){
                                   for(DataSnapshot users: dataSnapshot.getChildren()){
                                       User user = users.getValue(User.class);
                                       final ArrayList<String> hashtagLocal=addHashtags();
                                       if(fUser.getEmail().equals(user.getEmail())){
                                           Uri downloadUri = task.getResult();
                                           miUrlOk = downloadUri.toString();
                                           Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                                   miUrlOk,user,hashtagLocal);
                                           String uploadId = mDatabaseRef.push().getKey();
                                           mDatabaseRef.child(uploadId).setValue(upload);
                                           mHashTagRef=FirebaseDatabase.getInstance().getReference("hashtaging");
                                          mHashTagRef.addValueEventListener(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                          for(String h1:hashtagLocal)
                                                          {
                                                              if(!dataSnapshot.hasChild(h1))
                                                              {

                                                                  mHashTagRef.child(h1).setValue(h1);
                                                              }
                                                  }

                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                              }
                                          });



                                           pd.dismiss();
                                       }
                                   }
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });

                   } else {
                       Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                   }
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });

       } else {
           Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
       }
   }

   private void openImagesActivity() {

        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    private ArrayList<String> addHashtags()
    {
        Upload u=new Upload();
       String description=mDescription.getText().toString();
        Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
        Matcher mat = MY_PATTERN.matcher(description);
        ArrayList<String> ht=new ArrayList<String>();
        while (mat.find()) {
            ht.add(mat.group(1));
        }
        //u.setmHashtag(ht);
        return ht;
    }







}