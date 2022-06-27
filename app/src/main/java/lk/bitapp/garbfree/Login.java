package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private EditText email;
    private EditText password;
    private TextView doNotHaveAccount;
    private TextView needHelp;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.btn_login_login);
        email = findViewById(R.id.et_login_email);
        password = findViewById(R.id.et_login_password);
        doNotHaveAccount = findViewById(R.id.tv_doNotHaveAnAccount);
        needHelp = findViewById(R.id.tv_help);

        login.setOnClickListener(this);
        doNotHaveAccount.setOnClickListener(this);
        needHelp.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        //if user already logged in this will re direct to home interface
        if (firebaseAuth.getCurrentUser() != null) {
            //start home activity;
            startActivity(new Intent(Login.this, Home.class));
        }
    }

    @Override
    public void onClick(View view) {


        if (view == login) {
            UserLogin();
        }

        if (view == doNotHaveAccount) {
            //re direct to register interface;
            startActivity(new Intent(Login.this, Register.class));
        }

        if (view == needHelp) {
            //re dirct to help or password reset page;
            startActivity(new Intent(Login.this, Help.class));
        }
    }

    private void UserLogin() {

        String loginEmail = email.getText().toString().trim();
        String loginPassword = password.getText().toString().trim();

        if (loginEmail.isEmpty()) {
            Toast.makeText(Login.this, "Email is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loginPassword.isEmpty()) {
            Toast.makeText(Login.this, "Password is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Login, Please wait!");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //check wether the user email is successfully verified or not to continue the process;
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                //start home activity;
                                Toast.makeText(Login.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Home.class));
                                progressDialog.dismiss();
                            } else {
                                //this will tell user to click on the verification link in order to continue;
                                Toast.makeText(Login.this, "Please verify your email via the link sent to your email in order to proceed!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } else {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(Login.this, "Failed Login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return;
                        }

                    }
                });

    }
}
