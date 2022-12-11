package com.example.instatutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText fullNameEditText, emailEditText, passwordEditText;
    private TextView loginText;
    private MaterialButton doneButton;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize view variables
        initializeViews();

        // When the user presses the sign up text send them to the login screen
        loginText.setOnClickListener(view -> startActivity(new
                Intent(SignupActivity.this, LoginActivity.class)));

        // When the user presses the done button, they will be attempted to be signed in
        doneButton.setOnClickListener(view -> createUser());
    }

    private void initializeViews() {
        fullNameEditText = findViewById(R.id.full_name_signup_edittext);
        emailEditText = findViewById(R.id.email_signup_edittext);
        passwordEditText = findViewById(R.id.password_signup_edittext);
        doneButton = findViewById(R.id.sign_up_button_done);
        loginText = findViewById(R.id.signup_already_text);
    }

    private void createUser() {
        // Getting user input from edit text
        String fullNameInput = fullNameEditText.getText().toString();
        String emailAddressInput = emailEditText.getText().toString();
        String passwordInput = passwordEditText.getText().toString();

        // These are checks before the user can be registered
        if (fullNameInput.isEmpty()) {
            fullNameEditText.setError("Name cannot be empty");
        }
        else if (emailAddressInput.isEmpty()){
            emailEditText.setError("Email cannot be empty.");
            emailEditText.requestFocus();
        }
        else if (passwordInput.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            passwordEditText.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(emailAddressInput, passwordInput)
                    .addOnCompleteListener(task -> {
                        // The user has been successfully registered.
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                            // Make a local user object and a user document in Firebase.
                            User user = new User(fullNameInput, true, true, mAuth.getCurrentUser().getUid(), null, null);
                            db.collection("Users").document(user.getUserID()).set(user);

                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            finish();
                        }

                        // The user has been unsuccessfully registered.
                        else {
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}