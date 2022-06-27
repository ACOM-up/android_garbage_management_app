package lk.bitapp.garbfree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import lk.bitapp.garbfree.Helper.Transaction;

public class Grid_itemview extends AppCompatActivity {

    TextView name;
    ImageView image;
    EditText buyer;
    EditText qty;
    EditText cardNumber;
    EditText exDate;
    EditText ccv;
    EditText total;
    EditText bidding_quantity;
    EditText bidding_price;

    Intent intent;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int currentQTY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_itemview);
        intent = getIntent();

        name = findViewById(R.id.items);
        image = findViewById(R.id.images);
        buyer = findViewById(R.id.et_name);
        qty = findViewById(R.id.et_quantity);
        cardNumber = findViewById(R.id.et_cardNumber);
        exDate = findViewById(R.id.et_exDate);
        ccv = findViewById(R.id.et_ccv);
        total = findViewById(R.id.et_total);
        bidding_quantity = findViewById(R.id.bid_qty);
        bidding_price = findViewById(R.id.bid_price);

        name.setText(intent.getStringExtra("name"));
        Picasso.get().load(intent.getStringExtra("image")).into(image);
        currentQTY = Integer.parseInt(intent.getStringExtra("quantity"));

        final double tempPricePerItem = Double.parseDouble(intent.getStringExtra("price"));

        //get total by typing quantity
        qty.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.equals("") ) {
                    try {
                        total.setText((Integer.parseInt(qty.getText().toString())*tempPricePerItem)+"");
                    }catch (Exception ex){

                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
        //end get total
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

    public void btnBuyNowOnTap(View view) {

        try {
            if (!buyer.getText().toString().isEmpty() && !qty.getText().toString().isEmpty() && !cardNumber.getText().toString().isEmpty()
                    && !exDate.getText().toString().isEmpty() && !ccv.getText().toString().isEmpty()) {

                    if (!intent.getStringExtra("email").equalsIgnoreCase(getUserEmail())){

                        final int intQTY = Integer.parseInt(qty.getText().toString());
                            if (currentQTY > 0 && intQTY <= currentQTY) {
                                // Update quantity field
                                Map<String, Object> data = new HashMap<>();
                                data.put("quantity", (currentQTY - intQTY));


                                db.collection("items")
                                        .document(intent.getStringExtra("docId"))
                                        //Set options merge to update only the quantity field
                                        .set(data, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                //Inserting the transaction details
                                                Map<String, Object> transaction = new HashMap<>();
                                                transaction.put("itemId", intent.getStringExtra("docId"));
                                                transaction.put("itemName", intent.getStringExtra("name"));
                                                transaction.put("itemPrice", intent.getStringExtra("price"));
                                                transaction.put("quantity", intQTY + "");
                                                transaction.put("sellerUid", intent.getStringExtra("uid"));
                                                transaction.put("buyerUid", getUid());

                                                // Add a new document with a generated ID
                                                db.collection("transactions")
                                                        .add(transaction)
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

                                                                                    //merge values to the database
                                                                                    db.collection("rewards")
                                                                                            .document(getUserEmail())
                                                                                            .set(rewards, SetOptions.merge())
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    //Popup message
                                                                                                    try {
                                                                                                        //Popup message
                                                                                                        AlertDialog alertDialog = new AlertDialog.Builder(Grid_itemview.this).create();
                                                                                                        alertDialog.setTitle("CONGRATULATIONS..!!");
                                                                                                        alertDialog.setMessage("You Earned 10 points");
                                                                                                        alertDialog.setIcon(R.drawable.smilyface);
                                                                                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                                Toast.makeText(getApplicationContext(), "Your Rank Updated", Toast.LENGTH_SHORT).show();

                                                                                                                //On success, home page is shown
                                                                                                                startActivity(new Intent(Grid_itemview.this, Home.class));
                                                                                                            }
                                                                                                        });
                                                                                                        alertDialog.show();
                                                                                                    }catch (Exception ex){

                                                                                                    }
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Log.w("", "Error writing document", e);
                                                                                                }
                                                                                            });

                                                                                } else {
                                                                                    Log.d("Error", "get failed with ", task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e("ERROR", "Error adding document", e);
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("ERROR", "Error writing document", e);
                                            }
                                        });
                            }
                    }else {
                        Toast.makeText(getApplicationContext(),"Reminder : You are the Seller",Toast.LENGTH_SHORT).show();
                    }
            }else {
                Toast.makeText(getApplicationContext(),"Please complete All the fields", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Please enter valid data", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnBidNowOnTap(View view) {

        try {
            String sellerEmail = intent.getStringExtra("email");
            String itemName = intent.getStringExtra("name");
            String buyerEmail = getUserEmail();

            if (
                !bidding_price.getText().toString().isEmpty() &&
                !bidding_quantity.getText().toString().isEmpty() &&
                !sellerEmail.equalsIgnoreCase(buyerEmail)
            ) {
                Map<String, Object> biddings = new HashMap<>();
                biddings.put("price", bidding_price.getText().toString());
                biddings.put("quantity", bidding_quantity.getText().toString());
                biddings.put("buyerEmail", buyerEmail);
                biddings.put("sellerEmail", sellerEmail);
                biddings.put("isAccepted", false);
                biddings.put("itemId", intent.getStringExtra("docId"));
                biddings.put("itemName",itemName);

                db.collection("biddings")
                        .add(biddings)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //Popup message
                                try {
                                    //Popup message
                                    AlertDialog alertDialog = new AlertDialog.Builder(Grid_itemview.this).create();
                                    alertDialog.setTitle("Saved..!!");
                                    alertDialog.setMessage("You have placed a bidding");

                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //On success, home page is shown
                                            startActivity(new Intent(Grid_itemview.this, Home.class));
                                        }
                                    });
                                    alertDialog.show();
                                } catch (Exception ex) {

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error writing document", e);
                            }
                        });
            }else {
                Toast.makeText(getApplicationContext(),"Pleace Complete required fields",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Please enter valid data", Toast.LENGTH_SHORT).show();
        }
    }
}



