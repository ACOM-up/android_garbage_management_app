package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.bitapp.garbfree.Helper.Items;
import lk.bitapp.garbfree.Helper.Reward_items;

public class User_rewards extends AppCompatActivity {

    GridView gridView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Reward_items> lstReward = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rewards);

        getRewards();
    }

    private String getUserEmail() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                return user.getEmail();
            } else {
                return "";
            }
        } catch (Exception ex) {
            return "";
        }
    }

    //Retrieving reward details from the database
    private void getRewards() {

        db.collection("rewards")
                .document(getUserEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // getting map array from database
                        List<Map<String, Object>> tempHashList = (List<Map<String, Object>>) document.get("rewards");

                        // saving the retrieved hash map array to the array list (lstReward)
                        for (Map<String, Object> rewardItem : tempHashList) {
                            lstReward.add(
                                    new Reward_items(
                                            rewardItem.get("title").toString(),
                                            rewardItem.get("description").toString(),
                                            Boolean.parseBoolean(rewardItem.get("hasClaimed").toString()),
                                            rewardItem.get("createdDate").toString(),
                                            rewardItem.get("expireDate").toString()
                                    )
                            );
                        }
                        // end

                        gridView = findViewById(R.id.gridviewRewards);
                        CustomAdapter customAdapter = new CustomAdapter();
                        gridView.setAdapter(customAdapter);
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });

    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return lstReward.size();
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
            View view = getLayoutInflater().inflate(R.layout.rewards_row_data, null);
            TextView msg = view.findViewById(R.id.rew_msg);

            msg.setText(lstReward.get(position).getTitle() + " : " + lstReward.get(position).getDescription());
            return view;
        }
    }

    public void btnHomeOnTap(View view) {

        startActivity(new Intent(User_rewards.this, Home.class));
    }
}

