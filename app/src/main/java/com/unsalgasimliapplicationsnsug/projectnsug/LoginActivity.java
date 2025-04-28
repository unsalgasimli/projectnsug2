package com.unsalgasimliapplicationsnsug.projectnsug;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private EditText emailEt, passEt;
    private Button loginBtn;
    private TextView goRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // bind views
        mAuth      = FirebaseAuth.getInstance();
        emailEt    = findViewById(R.id.emailEt);
        passEt     = findViewById(R.id.passEt);
        loginBtn   = findViewById(R.id.loginBtn);
        goRegister = findViewById(R.id.goRegister);

        // LOGIN button handler
        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();
            String pass  = passEt.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurunuz", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signIn: SUCCESS user=" + mAuth.getCurrentUser().getEmail());
                            // only now navigate to MainActivity
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "signIn: FAILURE", task.getException());
                            Toast.makeText(this,
                                    "Giriş başarısız: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // "Kayıt Ol" link
        goRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
