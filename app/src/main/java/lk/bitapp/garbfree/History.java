package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import lk.bitapp.garbfree.Helper.Transaction;

public class History extends AppCompatActivity {

    GridView gridViewHistory;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Transaction> lstItems = new ArrayList<>();

    //gridViewHistory

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getItemData();
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

    //Retrieving item details from firestore
    private void getItemData(){
        db.collection("transactions")
                .whereEqualTo("buyerUid",getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                float price = Float.parseFloat(document.getData().get("itemPrice").toString());
                                int quantity = Integer.parseInt(document.getData().get("quantity").toString());
                                float total = price*quantity;

                                if (quantity > 0){
                                    lstItems.add(
                                            new Transaction(
                                                    document.getData().get("itemName").toString(),
                                                    price,
                                                    quantity,
                                                    total
                                            )
                                    );

                                    gridViewHistory = findViewById(R.id.gridviewHistory);

                                    CustomAdapter customAdapter = new CustomAdapter();
                                    gridViewHistory.setAdapter(customAdapter);

                                }
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
            return lstItems.size();
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
            View view = getLayoutInflater().inflate(R.layout.history_row_data,null);
            TextView name = view.findViewById(R.id.his_itemName);
            TextView price = view.findViewById(R.id.his_itemPrice);
            TextView quantity = view.findViewById(R.id.his_quantity);
            TextView total = view.findViewById(R.id.his_total);

            name.setText("Item name: " +lstItems.get(position).getItemName());
            price.setText("Item Price: " +"Rs."+lstItems.get(position).getItemPrice()+"");
            quantity.setText("Purchased Quantity: " +lstItems.get(position).getItemQty()+"");
            total.setText("Total Price: " +"Rs." +lstItems.get(position).getTotal()+"");


            return view;
        }
    }


}
