package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import lk.bitapp.garbfree.Helper.Items;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Buy_items extends AppCompatActivity {

    GridView gridView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Items> lstItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_items);

        getItemData();

    }


    //Retrieving item details from firestore
    private void getItemData(){
        db.collection("items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                float price = Float.parseFloat(document.getData().get("itemPrice").toString());
                                int quantity = Integer.parseInt(document.getData().get("quantity").toString());

                                if (quantity > 0){
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

                                    gridView = findViewById(R.id.gridviewHistory);

                                    CustomAdapter customAdapter = new CustomAdapter();
                                    gridView.setAdapter(customAdapter);

                                    //buy gridview single item on click event
                                    gridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(getApplicationContext(),Grid_itemview.class);
                                            intent.putExtra("docId",lstItems.get(position).getItemId()+"");
                                            intent.putExtra("name",lstItems.get(position).getiName()+"");
                                            intent.putExtra("price",lstItems.get(position).getiPrice()+"");
                                            intent.putExtra("quantity",lstItems.get(position).getQuantity()+"");
                                            intent.putExtra("location",lstItems.get(position).getLocation()+"");
                                            intent.putExtra("image",lstItems.get(position).getImg()+"");
                                            intent.putExtra("uid",lstItems.get(position).getUid()+"");
                                            intent.putExtra("email",lstItems.get(position).getEmail()+"");
                                            startActivity(intent);
                                        }
                                    });
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
            View view = getLayoutInflater().inflate(R.layout.row_data,null);
            TextView name = view.findViewById(R.id.buy_itemName);
            TextView price = view.findViewById(R.id.buyPrice);
            TextView location = view.findViewById(R.id.buy_location);
            ImageView image = view.findViewById(R.id.buyImage);

            name.setText(lstItems.get(position).getiName());
            price.setText("Price : Rs." +lstItems.get(position).getiPrice());
            location.setText("Location : " +lstItems.get(position).getLocation());
            Picasso.get().load(lstItems.get(position).getImg()).into(image);
            return view;
        }
    }
}
