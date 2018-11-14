package com.example.quizapp;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quizapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    MaterialEditText txtNewUserName, txtNewPassword, txtNewEmail; //Sign up
    MaterialEditText txtUserName, txtPassword; //Sign in

    private Button btnSignUp, btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        txtUserName = (MaterialEditText)findViewById(R.id.txtUserName);
        txtPassword = (MaterialEditText)findViewById(R.id.txtPassword);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(txtUserName.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    private void signIn(final String userName, final String password) {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userName).exists()) {
                    if(!userName.isEmpty()) {
                        User login = dataSnapshot.child(userName).getValue(User.class);
                        if(login.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Wrong username", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign up");
        alertDialog.setMessage("Please fill the information below");

        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout, null);

        txtNewPassword = (MaterialEditText)sign_up_layout.findViewById(R.id.txtNewPassword);
        txtNewUserName = (MaterialEditText)sign_up_layout.findViewById(R.id.txtNewUserName);
        txtNewEmail = (MaterialEditText)sign_up_layout.findViewById(R.id.txtNewEmail);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final User user = new User(txtNewUserName.getText().toString(),
                                    txtNewPassword.getText().toString(),
                                    txtNewEmail.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUserName()).exists()) {
                            Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            users.child(user.getUserName())
                                    .setValue(user);
                            Toast.makeText(MainActivity.this, "Registration success", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
