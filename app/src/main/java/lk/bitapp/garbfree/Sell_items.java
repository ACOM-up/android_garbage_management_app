package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import lk.bitapp.garbfree.Helper.Items;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class Sell_items extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    EditText iname, price, quantity, location;
    ImageView img;
    Button btnimage, btnsubmit;
    Items items;
    Uri downloadUri;
    String selectedImage = "";

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_items);

        iname = findViewById(R.id.et_iname);
        price = findViewById(R.id.et_iprice);
        quantity = findViewById(R.id.et_quantity);
        location = findViewById(R.id.et_location);

        img = findViewById(R.id.img_view);

        btnimage = findViewById(R.id.btn_image);
        btnsubmit = findViewById(R.id.btn_submit);

    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void btnImageChooseOnTap(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                //permission not granted, request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted
                pickImageFromGallery();
            }
        } else {
            pickImageFromGallery();
        }
    }

    //handle result of runtime permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //handle result of picked image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            img.setImageURI(data.getData());
            selectedImage = getFileName(data.getData());
        }
    }

    //Get Image name from URL
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getUid() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                return user.getUid();
            } else {
                return "";
            }
        } catch (Exception ex) {
            return "";
        }
    }

    private String getUserEmail(){
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                return user.getEmail();
            } else {
                return "";
            }
        }catch (Exception ex) {
            return "";
        }
    }

    private void uploadImageAndData() {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        final StorageReference itemRef = storageRef.child("images/items/" + selectedImage);

        // Get the data from an ImageView as bytes
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = itemRef.putBytes(data);


        if(!iname.getText().toString().isEmpty() && !price.getText().toString().isEmpty()&&
                !quantity.getText().toString().isEmpty() && !location.getText().toString().isEmpty()){

            //Upload Image + Data
            uploadTask
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            return;
                        }
                    })

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Getting Downloadable URL
                            uploadTask
                                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                throw task.getException();
                                            }

                                            // Continue with the task to get the download URL
                                            return itemRef.getDownloadUrl();
                                        }
                                    })

                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                downloadUri = task.getResult();

                                                //If image upload is success, insert item details to cloud firestore

                                                // Create a new item
                                                Map<String, Object> item = new HashMap<>();
                                                item.put("itemName", iname.getText().toString());
                                                item.put("itemPrice", price.getText().toString());
                                                item.put("quantity", quantity.getText().toString());
                                                item.put("location", location.getText().toString());
                                                item.put("image", downloadUri.toString());
                                                item.put("uid", getUid());
                                                item.put("email", getUserEmail());

                                                // Add a new document with a generated ID
                                                db.collection("items")
                                                        .add(item)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {

                                                                //add points to user using uid
                                                                DocumentReference docRef = db.collection("rewards").document(getUid());
                                                                docRef.get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    //The point value is incremented using FieldValue.increment(1)
                                                                                    Map<String, Object> rewards = new HashMap<>();
                                                                                    rewards.put("points", FieldValue.increment(10));
                                                                                    rewards.put("uid", getUid());
                                                                                    rewards.put("email", getUserEmail());

                                                                                    db.collection("rewards")
                                                                                            .document(getUserEmail())
                                                                                            .set(rewards, SetOptions.merge());
                                                                                }
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.e("ERROR", "Error adding document", e);
                                                                            }
                                                                        });

                                                                //for popup msg
                                                                popUp();


                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e("ERROR", "Error adding document", e);
                                                            }
                                                        });
                                            } else {
                                                return;
                                            }
                                        }
                                    });
                            //END Getting Downloadable URL

                        }
                    });
            //END Upload Image + Data

        }else {
            Toast.makeText(getApplicationContext(),"Please complete All the fields", Toast.LENGTH_SHORT).show();
        }

    }

    //buttonSubmit
    public void btnSubmitOnTap(View view) {
        try {

            uploadImageAndData();

        } catch (Exception ex) {

        }
    }

    //popup message for points
    public void popUp(){
        AlertDialog alertDialog = new AlertDialog.Builder(Sell_items.this).create();
        alertDialog.setTitle("CONGRATULATIONS..!!");
        alertDialog.setMessage("You Earned 10 points");
        alertDialog.setIcon(R.drawable.smilyface);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "You saved a life", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Sell_items.this, Home.class));
            }
        });

        alertDialog.show();
    }
}

