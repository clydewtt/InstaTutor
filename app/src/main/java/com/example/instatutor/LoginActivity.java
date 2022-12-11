package com.example.instatutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailEditText, passwordEditText;
    private TextView signupText;
    private MaterialButton doneButton;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize view variables
        initializeViews();

        // When the user presses the sign up text send them to the signup screen
        signupText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        // When the user presses the done button, they will be attempted to be logged in
        doneButton.setOnClickListener(view -> logUserIn());
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.email_login_edittext);
        passwordEditText = findViewById(R.id.password_login_edittext);
        doneButton = findViewById(R.id.login_button);
        signupText = findViewById(R.id.login_already_text);
    }

    private void logUserIn() {
        // Getting user input from edit texts
        String emailAddressInput = emailEditText.getText().toString();
        String passwordInput = passwordEditText.getText().toString();

        // These are checks before the user can be logged in
        if (emailAddressInput.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            emailEditText.requestFocus();
        }
        else if (passwordInput.isEmpty()) {
            passwordEditText.setError("Password cannot be empty.");
            passwordEditText.requestFocus();
        }
        else {
            mAuth.signInWithEmailAndPassword(emailAddressInput, passwordInput)
                    .addOnCompleteListener(task -> {
                        // The user has been successfully logged in.
                        if (task.isSuccessful()) {
                            // Send the user to the MainActivity.
                            Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        // The user has been unsuccessfully logged in.
                        else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}