package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private TextView logOut;
    private TextView tv_homepoints;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logOut=findViewById(R.id.tv_logout);
        logOut.setOnClickListener(this);
        tv_homepoints=findViewById(R.id.tv_homepoints);


        firebaseAuth=FirebaseAuth.getInstance();

        try {
            //get points to homepage
            DocumentReference docRef = db.collection("rewards").document(getUserEmail());
            docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String point = document.getData().get("points").toString();
                                    tv_homepoints.setText("Points : "+point);

                                } else {
                                    tv_homepoints.setText("");
                                }

                            } else {
                                Log.d("Error", "get failed with ", task.getException());
                            }
                        }
                    });

        }catch (Exception ex){

        }
    }

    @Override
    public void onClick(View view) {
        if (view==logOut){

           firebaseAuth.signOut();
           startActivity(new Intent(Home.this,Login.class));
        }
    }

    public void btnSellItemsOntap(View view) {
        startActivity(new Intent(Home.this,Sellitems_history.class));
    }

    public void btnBuyItemsOntap(View view) {
        startActivity(new Intent(Home.this,Buy_items.class));
    }

    public void btnHistoryOnTap(View view) {
        startActivity(new Intent(Home.this,History.class));
    }


    public void btnRewardsOnTap(View view) { startActivity(new Intent(Home.this,Rewards.class)); }

    public void btnMyBiddingsOntap(View view) { startActivity(new Intent(Home.this,MyAcceptedBiddings.class));}

    //get uid from firebase
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

}
