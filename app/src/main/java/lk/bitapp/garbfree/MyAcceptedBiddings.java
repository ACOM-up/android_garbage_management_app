package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.bitapp.garbfree.Helper.Biddings;

public class MyAcceptedBiddings extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GridView gridViewAcceptedBids;
    List <Biddings> lstAcceptedBids = new ArrayList<>();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_accepted_biddings);

        intent = getIntent();

        getMyBiddings();
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

    public void getMyBiddings(){

        db.collection("biddings")
                .whereEqualTo("buyerEmail",getUserEmail())
                .whereEqualTo("isAccepted",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for (QueryDocumentSnapshot document : task.getResult()){

                                int quantity = Integer.parseInt(document.getData().get("quantity").toString());
                                float price = Float.parseFloat(document.getData().get("price").toString());

                                lstAcceptedBids.add(
                                        new Biddings(
                                                document.getData().get("itemName").toString(),
                                                document.getData().get("itemId").toString(),
                                                document.getData().get("sellerEmail").toString(),
                                                quantity,
                                                price
                                        )
                                );

                                gridViewAcceptedBids = findViewById(R.id.gridviewAcceptedBiddings);

                                CustomAdapter customAdapter = new CustomAdapter();
                                gridViewAcceptedBids.setAdapter(customAdapter);
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
            return lstAcceptedBids.size();
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
            View view = getLayoutInflater().inflate(R.layout.my_accepted_biddings_row_data,null);
            TextView name = view.findViewById(R.id.my_bid_itemName);
            TextView userName = view.findViewById(R.id.my_bid_username);
            TextView quantity = view.findViewById(R.id.my_bid_quantity);
            TextView price = view.findViewById(R.id.my_bid_price);

            name.setText(lstAcceptedBids.get(position).getItemName());
            userName.setText(lstAcceptedBids.get(position).getUserName());
            quantity.setText("Quantity : " +lstAcceptedBids.get(position).getBidQuantity());
            price.setText(" Bid Price : Rs." +lstAcceptedBids.get(position).getBidPrice());

            return view;
        }
    }
}
