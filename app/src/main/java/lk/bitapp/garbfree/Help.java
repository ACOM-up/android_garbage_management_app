package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Help extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private Button resetPassword;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        email=findViewById(R.id.et_resetEmail);
        resetPassword=findViewById(R.id.btn_resetPassword);

        resetPassword.setOnClickListener(this);
        firebaseAuth=firebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        progressDialog.setMessage("Reset link is sending to your Email!Please wait.");
    }

    @Override
    public void onClick(View view) {
        if (view==resetPassword){

            String resetEmail=email.getText().toString().trim();

            if (resetEmail.isEmpty()){
                Toast.makeText(Help.this,"Email is emphty!",Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.show();
            firebaseAuth.sendPasswordResetEmail(resetEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        Toast.makeText(Help.this,"Password resent link has been sent to email! " +
                                "Please check the email.",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();


                        //after successfully send the reset link redirect to login interface;
                        startActivity(new Intent(Help.this,Login.class));
                    }

                    else {
                        Toast.makeText(Help.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }
            });

        }
    }
}
