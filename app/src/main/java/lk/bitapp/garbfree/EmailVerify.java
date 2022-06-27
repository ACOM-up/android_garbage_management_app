package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class EmailVerify extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private String signInEmail;
    private ProgressDialog progressDialog;

    private Button verify;
    private TextView emailOfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        verify = findViewById(R.id.btn_verify);
        emailOfUser = findViewById(R.id.tv_userEmail);

        verify.setOnClickListener(this);

        //get the current intent
        Intent intent = getIntent();
        //get the attached extras from the intent
        //we should use the same key as we used to attach the data.
        signInEmail = intent.getStringExtra("USER_EMAIL");
        emailOfUser.setText("Welcome " + signInEmail + "\n" + "please verify your email by clicking the link sent to your email to proceed!");

    }

    private void sendVerifyEmail() {

        progressDialog.show();

        firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(EmailVerify.this, "Email has been sent! Please check the email", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                            //after successfully registering and sending the email user need to login to the system.
                            startActivity(new Intent(EmailVerify.this,Login.class));

                        } else {
                            Toast.makeText(EmailVerify.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        if (view == verify) {

            sendVerifyEmail();

        }
    }
}