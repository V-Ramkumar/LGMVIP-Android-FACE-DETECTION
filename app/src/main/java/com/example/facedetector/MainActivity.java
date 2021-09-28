package com.example.facedetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    static int i=1;
    Button Btn1;
    ImageView imageView;
    private final static int FACECAPTURE=100;
    FirebaseVisionImage visionImage;
    FirebaseVisionFaceDetector visionFaceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        Btn1=findViewById(R.id.cambutton);
        imageView=findViewById(R.id.imageView);
        Btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intent,FACECAPTURE);


                }else {
                    Toast.makeText(MainActivity.this,"something wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FACECAPTURE && resultCode==RESULT_OK){
            Bundle bun= data.getExtras();
            Bitmap bitmap= (Bitmap) bun.get("data");
            detectface(bitmap);
            if (requestCode==FACECAPTURE){
                if (resultCode==RESULT_CANCELED){
                    i=1;
                }
            }
        }
    }

    private void detectface(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions detectorOptions=new FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE).
                setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS).
                setTrackingEnabled(true).
                setMinFaceSize(0.1f).
                setLandmarkType
                        (FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS).build();
        try {
            visionImage=FirebaseVisionImage.fromBitmap(bitmap);
            visionFaceDetector= FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions);

        }catch (Exception e){
            e.printStackTrace();


        }
        visionFaceDetector.detectInImage(visionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                String txt="";
                for (FirebaseVisionFace visionFace :firebaseVisionFaces){
                    txt=txt.concat("\n Face number"+i+":")
                            .concat("\n smile:"+visionFace.getSmilingProbability()*100+"%")
                            .concat("\n left eye open:"+visionFace.getLeftEyeOpenProbability()*100+"%")
                            .concat("\n right eye open:"+visionFace.getRightEyeOpenProbability()*100+"%");
                    i++;

                }
                if (firebaseVisionFaces.size()==0){
                    Toast.makeText(MainActivity.this,"face not detected",Toast.LENGTH_SHORT).show();
                }else {

                    Bundle bundle=new Bundle();
                    bundle.putString(LCOFaceDetection.TEXT,txt);
                    DialogFragment dialog=new ResultDialog();
                    dialog.setArguments(bundle);
                    dialog.setCancelable(true);
                    dialog.show(getSupportFragmentManager(),LCOFaceDetection.NAME);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"something wrong",Toast.LENGTH_SHORT).show();

            }
        });

    }
}