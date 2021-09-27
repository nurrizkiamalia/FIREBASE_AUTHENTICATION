package com.TiB.lifeandchoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    TextView reg, lupa, vrf1, vrf2;
    EditText mail, pw;
    Button lgn;
    FirebaseAuth fAuth;
    ProgressBar pgbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        reg = findViewById(R.id.toReg);
        lgn = findViewById(R.id.btn_login);
        mail = findViewById(R.id.et_mail);
        pw = findViewById(R.id.et_passwd);
        lupa = findViewById(R.id.lupapass);

        fAuth = FirebaseAuth.getInstance();
        pgbar = findViewById(R.id.progressBar);
        FirebaseUser fuser = fAuth.getCurrentUser();

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }

        lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString().trim();
                String password = pw.getText().toString().trim();

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

                pgbar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if(fAuth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(login.this,"Anda telah masuk ke Akun.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),Home.class));
                                pgbar.setVisibility(View.GONE);
                                finish();
                            }
                            else{
                                Toast.makeText(login.this,"Anda Belum Mem-verifikasi E-mail! Cek Email anda atau cek apakah Email yang anda daftarkan sudah benar! ",Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else{
                            Toast.makeText(login.this,"Error! "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            pgbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        reg.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(),registration.class));
            finish();
        });

        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail = new EditText(v.getContext());
                resetmail.setPadding(50,20,50,20);
                resetmail.setBackground(getDrawable(R.drawable.container_round_main));
                final AlertDialog.Builder passreset = new AlertDialog.Builder(v.getContext());
                passreset.setTitle("Reset Password?");
                passreset.setMessage("Masukkan Email untuk Menerima Reset Link");
                passreset.setView(resetmail);

                passreset.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetmail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(fAuth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(login.this, "Reset Link Telah dikirim ke Email Anda.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(login.this, "Email Belum di-Verifikasi.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this,"Error! Reset Link Tidak Terkirim"+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                passreset.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passreset.create().show();
            }
        });
    }
}