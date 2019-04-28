package edmt.dev.androidmp3stream;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Main2Activity extends AppCompatActivity {


    private ProgressDialog mDialog;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    MediaPlayer mediaPlayer;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    private ImageButton micButton;
    private StorageReference mStorage;
    public static final String TAG = Main2Activity.class.getSimpleName();
    private File mediaStorageDir;
    private Button mStart, mStop;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        random = new Random();
        mStorage = FirebaseStorage.getInstance().getReference();


        mDialog = new ProgressDialog(this);


        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Recorder");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory");
            }
        }
   //Init
        mStart = findViewById(R.id.start_button);
        mStop = findViewById(R.id.stop_button);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAudio();
                Toast.makeText(getApplicationContext(),"recording",Toast.LENGTH_SHORT).show();
            }
        });


        //The button to stop the audio recording
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });



        //micButton = findViewById(R.id.mic);

//
//        micButton.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (checkPermission()) {
//                    int count = 0;
//                    AudioSavePathInDevice = mediaStorageDir.getAbsolutePath() + "/" + "AudioRecording" + count + ".3gp";
//                    Log.d(TAG, "on touch listener" + AudioSavePathInDevice);
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        Toast.makeText(Main2Activity.this, "recording", Toast.LENGTH_SHORT).show();
//
//                        MediaRecorderReady();
//                        try {
//
//                            Toast.makeText(Main2Activity.this, AudioSavePathInDevice, Toast.LENGTH_SHORT).show();
//                            mediaRecorder.prepare();
//                            mediaRecorder.start();
//                        } catch (IllegalStateException e) {
//
//                            e.printStackTrace();
//                        } catch (IOException e) {
//
//                            e.printStackTrace();
//                        }
//
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        mediaRecorder.stop();
//                        Toast.makeText(getApplicationContext(), "recording Stopped", Toast.LENGTH_SHORT).show();
//                        mediaPlayer = new MediaPlayer();
//
//                        try {
//                            mediaPlayer.setDataSource(AudioSavePathInDevice);
//                            // uploadAudio();
//                            mediaPlayer.prepare();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        mediaPlayer.start();
//                        uploadAudio();
//                        Toast.makeText(Main2Activity.this, "Recording Playing",
//                                Toast.LENGTH_LONG).show();
//                        Toast.makeText(Main2Activity.this, AudioSavePathInDevice, Toast.LENGTH_SHORT).show();
//
//
//                    }
//                } else {
//                    requestPermission();
//                }
//
//                return true;
//            }
//        });
    }


    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

//Requesting permission for a new user
    private void requestPermission() {
        ActivityCompat.requestPermissions(Main2Activity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(Main2Activity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Main2Activity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
  //The method to upload the video to the firebase storage
    private void uploadAudio() {
        Toast.makeText(getApplicationContext(), "Uploading", Toast.LENGTH_SHORT).show();
        mDialog.setMessage("uploading audio");
        mDialog.show();

        StorageReference filename = mStorage.child("Audio").child("new_audio.3gp");
        Uri uri = Uri.fromFile(new File(AudioSavePathInDevice));
        Log.d(TAG, "Upload method  " + String.valueOf(uri.toString()));

        filename.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDialog.dismiss();

            }
        });
    }

//The method to start the audio recording
    private void startAudio() {
        if (checkPermission()) {
            int count = 0;
            AudioSavePathInDevice = mediaStorageDir.getAbsolutePath() + "/" + "AudioRecording" + count + ".3gp";
            Toast.makeText(Main2Activity.this, "recording", Toast.LENGTH_SHORT).show();

            MediaRecorderReady();
            try {

                Toast.makeText(Main2Activity.this, AudioSavePathInDevice, Toast.LENGTH_SHORT).show();
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

        } else {
            requestPermission();
        }

    }


    //The method to stop the audio recording an automatically save it to database
    private void stopAudio() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            uploadAudio();
        }

    }
}
