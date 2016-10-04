package com.firebase.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.BaseActivity;
import com.firebase.MainActivity;
import com.firebase.R;
import com.firebase.Utility;
import com.firebase.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUpActivity extends BaseActivity {

    private EditText editFirstName, editLastName, editSignUpEmail, editMobileNumber, editSignUpPassword, editSignUpConfirmPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        //For Signup
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editSignUpEmail = (EditText) findViewById(R.id.editSignUpEmail);
        editMobileNumber = (EditText) findViewById(R.id.editMobileNumber);
        editSignUpPassword = (EditText) findViewById(R.id.editSignUpPassword);
        editSignUpConfirmPassword = (EditText) findViewById(R.id.editSignUpConfirmPassword);

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable())
                    signUp();
                else showNetworkError();
            }
        });


    }


    private void signUp() {

        if (isValidateSignUp()) {
            showProgress();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                final FirebaseUser userInfo = task.getResult().getUser();
                                if (userInfo != null) {

                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {

                                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                                                        database = database.child(userInfo.getUid());

                                                        final UserModel userModel = new UserModel();
                                                        userModel.setFirst_name(firstName);
                                                        userModel.setLast_name(lastName);
                                                        userModel.setEmail(email);
                                                        userModel.setMobile_number(mobileNumber);
                                                        database.setValue(userModel);

                                                        database.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                hideProgress();
                                                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                                if (null != user) {
                                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finishAffinity();
                                                                    Toast.makeText(SignUpActivity.this, "Your registration has been successfully done.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                hideProgress();
                                                                Log.d("==>","==>"+databaseError);
                                                            }
                                                        });


                                                    } else {
                                                        hideProgress();
                                                        Utility.alert(SignUpActivity.this, null, task.getException().getMessage());
                                                    }
                                                }
                                            });


                                } else {
                                    hideProgress();
                                    Utility.alert(SignUpActivity.this, null, task.getException().getMessage());
                                }


                            } else {
                                hideProgress();
                                Utility.alert(SignUpActivity.this, null, task.getException().getMessage());
                            }
                        }

                    });

        }
    }

    private String firstName, lastName, email, mobileNumber, password;

    private boolean isValidateSignUp() {

        boolean isValidate = true;

        firstName = editFirstName.getText().toString().trim();
        lastName = editLastName.getText().toString().trim();
        email = editSignUpEmail.getText().toString().trim();
        mobileNumber = editMobileNumber.getText().toString().trim();
        password = editSignUpPassword.getText().toString().trim();
        String confirm_password = editSignUpConfirmPassword.getText().toString().trim();

        if (firstName.length() == 0) {
            editFirstName.setError("Required Field");
            isValidate = false;
        }
        if (lastName.length() == 0) {
            editLastName.setError("Required Field");
            isValidate = false;
        }

        if (email.length() == 0) {
            editSignUpEmail.setError("Required Field");
            isValidate = false;
        } else if (!Utility.isValidEmail(email)) {
            editSignUpEmail.setError("Please enter valid Email Id.");
            isValidate = false;
        }

        if (mobileNumber.length() == 0) {
            editMobileNumber.setError("Required Field");
            isValidate = false;
        }

        if (password.length() == 0) {
            editSignUpPassword.setError("Required Field");
            isValidate = false;
        } else if (password.length() < 8) {
            editSignUpPassword.setError("Please enter password with minimum 8 characters");
            isValidate = false;
        }

        if (confirm_password.length() == 0) {
            editSignUpConfirmPassword.setError("Required Field");
            isValidate = false;
        } else if (!password.equals(confirm_password)) {
            editSignUpConfirmPassword.setError("Confirm Password do not match.");
            isValidate = false;
        }
        return isValidate;
    }


}
