package com.example.instatutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rolud.solidglowanimation.SolidGlowAnimation;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private SolidGlowAnimation glowAnimation;
    private CircularTextView circularTextView;
    private ImageView accountButton;

    private final ArrayList<Subject> subjects = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();

    private ListView topicList;
    private Dialog subjectDialog;

    private boolean isSearchingForTutor = false;
    private boolean isTutorFound = false;
    private int index = 0;
    private Subject subject;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all the views in the activity
        initializeViews();

        // Load information from database
        loadSubjects();

        glowAnimation.startAnimation();
        circularTextView.setSolidColor("#385FEB");

        circularTextView.setOnClickListener(view -> {
            // Open dialog to narrow down search
            if (!isSearchingForTutor) {
                openSubjectDialog();
            }
        });

        // When the account button is clicked, send the user to their profile screen.
        accountButton.setOnClickListener(view -> startActivity(new Intent(
                MainActivity.this, ProfileActivity.class)));
    }

    // Initialize all the views in the activity
    private void initializeViews() {
        glowAnimation = findViewById(R.id.glow_animation);
        circularTextView = findViewById(R.id.circular_textview);
        accountButton = findViewById(R.id.account_button);
    }

    // Loads all of the subject data from the database.
    private void loadSubjects() {
        db.collection("Subjects").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get all subjects from database
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Subject subject = document.toObject(Subject.class);
                    subjects.add(subject);
                    Log.e("Subject name", subject.getSubjectName());
                }

                Log.e("Subjects", "loaded successfully " + subjects.size());

            } else {
                Toast.makeText(MainActivity.this, "Error getting subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Opens a dialog which allows the user to narrow down their search for a tutor to a specific
    // subject and topic.
    private void openSubjectDialog() {
        subjectDialog = new Dialog(MainActivity.this);
        subjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        subjectDialog.setCancelable(true);
        subjectDialog.setContentView(R.layout.choose_tutor_subject_dialog);

        // Get the views from the dialog layout
        MaterialButton doneButton = subjectDialog.findViewById(R.id.done_button_choose_subject);
        ImageButton backButton = subjectDialog.findViewById(R.id.subject_back_button);
        ImageButton nextButton = subjectDialog.findViewById(R.id.subject_next_button);
        topicList = subjectDialog.findViewById(R.id.subject_subtopics_listview);
        TextView currentSubject = subjectDialog.findViewById(R.id.subject_textview);

        doneButton.setOnClickListener(view -> subjectDialog.dismiss());

        // Making the dialog take up most of the user's screen width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(subjectDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        // Update dialog UI
        updateDialog(currentSubject);

        // Implementing back button functionality
        backButton.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                updateDialog(currentSubject);
            }
        });

        // Implementing next button functionality
        nextButton.setOnClickListener(view -> {
            if (index < subjects.size() - 1) {
                index += 1;
                updateDialog(currentSubject);
            } else if (index == subjects.size() - 2) {
                nextButton.setEnabled(false);
            }
        });

        subjectDialog.show();
        subjectDialog.getWindow().setAttributes(lp);

    }

    // Updates the UI for the Dialog
    private void updateDialog(TextView currentSubject) {
        // Updates UI elements
        subject = subjects.get(index);
        currentSubject.setText(subject.getSubjectName());
        TopicsListAdapter listAdapter = new TopicsListAdapter();
        topicList.setAdapter(listAdapter);
    }

    // Finds a tutor for the user and matches them to them
    private void findTutor(String subjectName, String subtopic) {
        subjectDialog.dismiss();
        circularTextView.setText("Searching...");
        isSearchingForTutor = true;

        Log.e("FIND", subjectName + subtopic);

        // Get other user information and only keep the ones who fit the needs of the current user.
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    User user = documentSnapshot.toObject(User.class);

                    boolean doesUserTeachCurrentSubject = false;

                    for (String tutoringSubject : user.getTutoringSubjects()) {
                        if (tutoringSubject.equals(subtopic)) {
                            doesUserTeachCurrentSubject = true;
                            break;
                        }
                    }

                    // Sorting the users
                    if (user.isOnline() && user.isTutor() && doesUserTeachCurrentSubject && !Objects.equals(user.getUserID(), mAuth.getUid())) {
                        users.add(user);
                    }
                }

                // If there is a match, then try to connect to a video call
                if (users.size() != 0) {
                    isTutorFound = true;
                    Toast.makeText(this, "A tutor was found!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this, MeetingActivity.class));

                } else {
                    isTutorFound = false;
                    Toast.makeText(this, "No tutors were found", Toast.LENGTH_SHORT).show();
                }

                Log.e("Num of user matches", String.valueOf(users.size()));
            } else {
                Log.e("TUTOR", "Could not access tutors");
            }
        });

        updateUI();
    }

    private void updateUI() {
        if (isTutorFound) {
            circularTextView.setText("Tutor Found.");
        } else {
            circularTextView.setText("Find Tutor");
        }
    }

    // List Adapter for displaying the different subtopics under each subject.
    private class TopicsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return subject.getSubtopics().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View listItemLayout = getLayoutInflater().inflate(R.layout.topic_list_item, null);

            // The views from the custom layout that I made
            LinearLayout card = listItemLayout.findViewById(R.id.topic_card);
            TextView topicView = listItemLayout.findViewById(R.id.topic_textview_list_item);

            topicView.setText(subject.getSubtopics().get(i));

            card.setOnClickListener(view1 -> findTutor(subject.getSubjectName(),
                    topicView.getText().toString()));

            return listItemLayout;
        }
    }
}