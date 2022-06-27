package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Rewards extends AppCompatActivity {

    private TextView tv_points;
    private TextView tv_rank;
    private TextView tv_rewards;
    private Button btn_details;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        tv_points = findViewById(R.id.txt_points);
        tv_rank = findViewById(R.id.txt_rank);
        tv_rewards = findViewById(R.id.txt_reward);

        firebaseAuth = FirebaseAuth.getInstance();

        try {
            //get points
            DocumentReference docRef = db.collection("rewards").document(getUserEmail());
            docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    String points = document.get("points").toString();
                                    tv_points.setText("Total Points : "+points);
                                }else{
                                    tv_points.setText("");
                                }
                            }else {
                                Log.d("ERROR","get failed with",task.getException());
                            }
                        }
                    });
        }catch (Exception ex){

        }

        //set rank
        DocumentReference docref = db.collection("rewards").document(getUserEmail());
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        String points = document.get("points").toString();
                        if (Integer.parseInt(points) >= 100){
                            tv_rank.setText("Rank : 01");
                        }else if (Integer.parseInt(points) >= 200){
                            tv_rank.setText("Rank : 02");
                        }else if (Integer.parseInt(points) >= 300){
                            tv_rank.setText("Rank : 03");
                        }else if (Integer.parseInt(points) >= 400){
                            tv_rank.setText("Rank : 04");
                        }else if (Integer.parseInt(points) >= 500){
                            tv_rank.setText("Rank : 05");
                        }else{
                            tv_rank.setText("Rank : No Rank");
                        }
                    }
                }
            }
        });
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

    public void btnClaimRewardOnTap(View view) {

//        AlertDialog alertDialog = new AlertDialog.Builder(Rewards.this).create();
//        alertDialog.setTitle("CONGRATULATIONS..!!");
//        alertDialog.setMessage("You Won the Monthly Price");
//        alertDialog.setIcon(R.drawable.star);
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        alertDialog.show();

        startActivity(new Intent(Rewards.this,User_rewards.class));
    }
}
