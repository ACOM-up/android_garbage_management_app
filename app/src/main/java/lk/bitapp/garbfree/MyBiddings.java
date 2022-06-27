package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.bitapp.garbfree.Helper.Biddings;

public class MyBiddings extends AppCompatActivity {

    GridView gridViewBid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Biddings> lstBidDetails = new ArrayList<>();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_biddings);

        intent = getIntent();

        getBidData();
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

    public void getBidData(){
        db.collection("biddings")
                .whereEqualTo("itemId",intent.getStringExtra("itemId"))
                .whereEqualTo("isAccepted",false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()){

                                    int quantity = Integer.parseInt(document.getData().get("quantity").toString());
                                    float price = Float.parseFloat(document.getData().get("price").toString());
                                    String itemName = intent.getStringExtra("itemName");

                                    lstBidDetails.add(
                                            new Biddings(
                                                    document.getId(),
                                                    itemName,
                                                    document.getData().get("buyerEmail").toString(),
                                                    quantity,
                                                    price
                                            )
                                    );

                                    gridViewBid = findViewById(R.id.gridviewBidding_items);

                                    CustomAdapter customAdapter = new CustomAdapter();
                                    gridViewBid.setAdapter(customAdapter);

                            }

                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                    }

                    }
                });
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return lstBidDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int clickedPosition = position;
            View view = getLayoutInflater().inflate(R.layout.my_biddings_row_data,null);
            TextView name = view.findViewById(R.id.bid_itemName);
            TextView userName = view.findViewById(R.id.bid_username);
            TextView quantity = view.findViewById(R.id.bid_quantity);
            TextView price = view.findViewById(R.id.bid_price);
            Button btnAccept = view.findViewById(R.id.btnAccept);

            name.setText(lstBidDetails.get(position).getItemName());
            userName.setText(lstBidDetails.get(position).getUserName());
            quantity.setText("Quantity : " +lstBidDetails.get(position).getBidQuantity());
            price.setText(" Bid Price : Rs." +lstBidDetails.get(position).getBidPrice());


            //Bidding Accept button Onclick
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String biddingId = lstBidDetails.get(clickedPosition).getBiddingId();


                    Map<String, Object> biddings = new HashMap<>();
                    biddings.put("isAccepted", true);

                    db.collection("biddings")
                            .document(biddingId)
                            .set(biddings, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Popup message
                                    try {
                                        //Popup message
                                        AlertDialog alertDialog = new AlertDialog.Builder(MyBiddings.this).create();
                                        alertDialog.setTitle("Saved..!!");
                                        alertDialog.setMessage("You have Accepted a bidding");

                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //On success, home page is shown
                                                startActivity(new Intent(MyBiddings.this, Home.class));
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

                }
            });

            return view;
        }
    }


}
