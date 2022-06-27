package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lk.bitapp.garbfree.Helper.Items;
import lk.bitapp.garbfree.Helper.Transaction;

public class Sellitems_history extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Items> lstItems = new ArrayList<>();

    GridView gridview_sellitems_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellitems_history);

        gridview_sellitems_history = findViewById(R.id.sellitems_history);

        getSellItems();
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
    public void getSellItems() {
        db.collection("items")
                .whereEqualTo("uid", getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                float price = Float.parseFloat(document.getData().get("itemPrice").toString());
                                int quantity = Integer.parseInt(document.getData().get("quantity").toString());

                                lstItems.add(
                                        new Items(
                                                document.getId(),
                                                document.getData().get("itemName").toString(),
                                                price,
                                                quantity,
                                                document.getData().get("location").toString(),
                                                document.getData().get("image").toString(),
                                                document.getData().get("uid").toString(),
                                                document.getData().get("email").toString()
                                        )
                                );

                                CustomAdapter customAdapter = new CustomAdapter();
                                gridview_sellitems_history.setAdapter(customAdapter);

                                //sellItem gridview single item on click event
                                gridview_sellitems_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(getApplicationContext(),MyBiddings.class);
                                        intent.putExtra("itemId",lstItems.get(position).getItemId()+"");
                                        intent.putExtra("itemName",lstItems.get(position).getiName()+"");
                                        startActivity(intent);
                                    }
                                });
                            }

                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void btnAddNew(View view) {
        try {
            startActivity(new Intent(Sellitems_history.this,Sell_items.class));

        } catch (Exception ex) {

        }
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
            View view = getLayoutInflater().inflate(R.layout.row_data,null);
            TextView name = view.findViewById(R.id.buy_itemName);
            TextView price = view.findViewById(R.id.buyPrice);
            TextView quantity = view.findViewById(R.id.buy_location);
            ImageView image = view.findViewById(R.id.buyImage);

            name.setText(lstItems.get(position).getiName());
            price.setText("Price : Rs." +lstItems.get(position).getiPrice());
            quantity.setText("Quantity :" +lstItems.get(position).getQuantity());
            Picasso.get().load(lstItems.get(position).getImg()).into(image);

            return view;
        }
    }
}
