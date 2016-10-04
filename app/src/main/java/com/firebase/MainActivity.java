package com.firebase;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showProgress();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
        database = database.child(user.getUid());
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgress();
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                ((TextView) findViewById(R.id.textView)).setText("Hello " + userModel.getFirst_name() + " " + userModel.getLast_name());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
                Log.d("==>", "==>" + databaseError);
            }
        });


    }
}
