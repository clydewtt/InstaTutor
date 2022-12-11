package com.example.instatutor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.react.modules.core.PermissionListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.net.MalformedURLException;
import java.net.URL;

public class MeetingActivity extends AppCompatActivity implements JitsiMeetActivityInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        try {
            connectToVideoCall();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private void connectToVideoCall() throws MalformedURLException {
        JitsiMeetView view = new JitsiMeetView(MeetingActivity.this);
        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(new URL("https://meet.jit.si"))
                .setRoom("hackathontest")
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .build();

        view.join(options);
        setContentView(view);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}