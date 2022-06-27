package lk.bitapp.garbfree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button submit;
    private EditText editTextemail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextUserName;
    private TextView alreadyHaveAnAccount;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=firebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);

        submit=findViewById(R.id.btn_submit);
        editTextemail=findViewById(R.id.et_email);
        editTextPassword=findViewById(R.id.et_password);
        editTextConfirmPassword=findViewById(R.id.et_confirmPassword);
        editTextUserName=findViewById(R.id.et_username);
        alreadyHaveAnAccount=findViewById(R.id.tv_signIn);

        submit.setOnClickListener(this);
        alreadyHaveAnAccount.setOnClickListener(this);

        //if user already logged in this will re direct to home interface
        if (firebaseAuth.getCurrentUser()!=null){
            //start home activity;
            startActivity(new Intent(Register.this, Home.class));
        }

    }

    private void  registerUser(){

        String email=editTextemail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String confirmPassword=editTextConfirmPassword.getText().toString().trim();
        String userName=editTextUserName.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            //email is emphty
            Toast.makeText(this,"Please insert Email!",Toast.LENGTH_SHORT).show();
            return;//return will execute further execution of the function
        }

        if (TextUtils.isEmpty(userName)){
            //email is emphty
            Toast.makeText(this,"Please insert user name!",Toast.LENGTH_SHORT).show();
            return;//return will execute further execution of the function
        }

        if (TextUtils.isEmpty(password)){
            //password is emphty
            Toast.makeText(this,"Please insert password!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)){
            //password is emphty
            Toast.makeText(this,"Please retype the password!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(password.equals(confirmPassword))){
            //passwords are not matching
            Toast.makeText(this,"password and confirm password are not matching!",Toast.LENGTH_SHORT).show();
            return;
        }

        //if validations are okay  then we will show a progress bar

        progressDialog.setMessage("You are registering, Please wait!");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //user registerd successfully
                    Toast.makeText(Register.this,"Registerd Successfully!",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                   // startActivity(new Intent(Register.this, Login.class));

                    //if all good then go to verification page;
                    verify();
                }
                else{
                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
                    Toast.makeText(Register.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();

                    //user registration failed
                   //Toast.makeText(Register.this,"Error in registration!",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void verify(){
        // startActivity(new Intent(Register.this,EmailVerify.class)
        // .putExtra("User Email",editTextemail.getText().toString().trim()));

        //creating and initializing an Intent object
        Intent intent = new Intent(Register.this,EmailVerify.class);

        //attach the key value pair using putExtra to this intent
        String email = editTextemail.getText().toString().trim();
        intent.putExtra("USER_EMAIL",email);

        //starting the activity
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        if (view==submit){
            //Here new user will register through register user function;
            registerUser();
        }

        if (view==alreadyHaveAnAccount){
            //direct to login page;
            startActivity(new Intent(Register.this,Login.class));
        }

    }
}

