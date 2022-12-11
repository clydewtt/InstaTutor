package com.example.instatutor;

import androidx.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private CheckBox isOnlineCheckBox;
    private TextView username, userStatus;
    private MaterialButton addTutoringCoursesButton;
    private TutoringSubjectsListAdapter listAdapter;
    private ImageButton backButton;
    private DisplayListAdapter displayListAdapter;
    private Dialog coursesDialog;
    private ListView topicList, tutoringSubjects;
    private Subject subject;
    private int index = 0;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<String> tutoringCourses = new ArrayList<>();
    private final DocumentReference userRef = db.collection("Users").document(Objects.requireNonNull(mAuth.getUid()));
    private final ArrayList<Subject> subjects = new ArrayList<>();

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();

        // Load subjects for adding tutoring classes.
        loadSubjects();

        addTutoringCoursesButton.setOnClickListener(view -> openCoursesDialog());

        backButton.setOnClickListener(view -> onBackPressed());

        // Allows user to change online status
        isOnlineCheckBox.setOnClickListener(view -> {
            if (isOnlineCheckBox.isChecked()) {
                userRef.update("online", true).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Status changed to online!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                userRef.update("online", false).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Status changed to offline!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initializeViews() {
        isOnlineCheckBox = findViewById(R.id.is_online_checkbox);
        addTutoringCoursesButton = findViewById(R.id.add_tutoring_subjects_button);
        backButton = findViewById(R.id.back_button_profile);
        userStatus = findViewById(R.id.user_status);
        tutoringSubjects = findViewById(R.id.tutoring_subjects_listview);

        db.collection("Users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    tutoringCourses = (ArrayList<String>) task.getResult().get("tutoringSubjects");
                    currentUser = task.getResult().toObject(User.class);
                }
                displayListAdapter = new DisplayListAdapter();
                tutoringSubjects.setAdapter(displayListAdapter);
            }
        });
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

                if (subjects.size() != 0) {
                    subject = subjects.get(0);
                }

                Log.e("Subjects", "loaded successfully " + subjects.size());

            } else {
                Toast.makeText(ProfileActivity.this, "Error getting subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCoursesDialog() {
        coursesDialog = new Dialog(ProfileActivity.this);
        coursesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        coursesDialog.setCancelable(true);
        coursesDialog.setContentView(R.layout.choose_tutor_subject_dialog);

        // Get the views from the dialog layout
        MaterialButton doneButton = coursesDialog.findViewById(R.id.done_button_choose_subject);
        ImageButton backButton = coursesDialog.findViewById(R.id.subject_back_button);
        ImageButton nextButton = coursesDialog.findViewById(R.id.subject_next_button);
        topicList = coursesDialog.findViewById(R.id.subject_subtopics_listview);
        TextView currentSubject = coursesDialog.findViewById(R.id.subject_textview);

        doneButton.setOnClickListener(view -> coursesDialog.dismiss());

        // Making the dialog take up most of the user's screen width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(coursesDialog.getWindow().getAttributes());
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

        coursesDialog.show();
        coursesDialog.getWindow().setAttributes(lp);
    }

    // Updates the UI for the Dialog
    private void updateDialog(TextView currentSubject) {
        // Updates UI elements
        subject = subjects.get(index);
        currentSubject.setText(subject.getSubjectName());
        listAdapter = new TutoringSubjectsListAdapter();
        topicList.setAdapter(listAdapter);
    }

    // Adds a course to the user's list of courses that they can tutor in
    private void addTutoringCourse(String course) {
        userRef.update("tutoringSubjects", FieldValue.arrayUnion(course)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "You can now tutor in: " + course, Toast.LENGTH_SHORT).show();
                tutoringCourses.add(course);
                displayListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ProfileActivity.this, "An error has occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // List Adapter for displaying the tutoring classes that each student has.
    private class DisplayListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tutoringCourses.size();
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
            TextView topicView = listItemLayout.findViewById(R.id.topic_textview_list_item);

            topicView.setText(tutoringCourses.get(i));

            return listItemLayout;
        }
    }

    // List Adapter for displaying the available classes to teach from
    private class TutoringSubjectsListAdapter extends BaseAdapter {

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

            card.setOnClickListener(view1 -> addTutoringCourse(topicView.getText().toString()));

            return listItemLayout;
        }
    }
}