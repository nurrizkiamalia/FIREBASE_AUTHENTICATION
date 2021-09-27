package com.TiB.lifeandchoice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText nama, mail, pw, nmrtlp;
    FirebaseAuth fAuth;
    Button regis;
    TextView lgn;
    ProgressBar pgbar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        lgn = findViewById(R.id.toLogin);
        nama = findViewById(R.id.et_fullname);
        mail = findViewById(R.id.et_mail);
        pw = findViewById(R.id.et_passwd);
        nmrtlp = findViewById(R.id.et_phonenumb);
        regis = findViewById(R.id.btn_regis);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        pgbar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nm = nama.getText().toString().trim();
                String email = mail.getText().toString().trim();
                String password = pw.getText().toString().trim();
                String pon = nmrtlp.getText().toString().trim();

                if (TextUtils.isEmpty(nm)){
                    nama.setError("Nama harus diisi!");
                    return;
                }

                if (nm.length() > 20){
                    nama.setError("Nama terlalu panjang");
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    mail.setError("Email tidak boleh kosong!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    pw.setError("Password tidak boleh kosong!");
                    return;
                }

                if (password.length() < 8){
                    pw.setError("Password kurang dari 8 karakter!");
                    return;
                }

                if (TextUtils.isEmpty(pon)){
                    nmrtlp.setError("Nomor telepon harus diisi!");
                    return;
                }

                pgbar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(registration.this,"Akun Telah Dibuat.",Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference docref = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", nm);
                            user.put("email", email);
                            user.put("phone", pon);
                            docref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"OnSuccess: Profil user telah dibuat "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","onFailure : "+e.getMessage());
                                }
                            });

                            //verifikasi

                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(registration.this, "Email verifikasi telah dikirim.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","onFailure: Email tidak terkirim "+ e.getMessage());
                                }
                            });

                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(),verify.class));
                            finish();

                        } else {
                            Toast.makeText(registration.this,"Error!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            pgbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        lgn.setOnClickListener((v)-> {
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        });


    }
}