package edu.skku2.map.lotto_kotlin

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import org.w3c.dom.Text
import java.io.File

@SuppressLint("Registered")
class Viewresult : AppCompatActivity() {
    @SuppressLint("WrongThread", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        var abc=""
        var abcd=""
        super.onCreate(savedInstanceState)
        val filepath=intent.getStringExtra("filepath")
        setContentView(R.layout.viewresult)
        val file= File(filepath)
        val decode=ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
        val bitmap=ImageDecoder.decodeBitmap(decode)
        findViewById<ImageView>(R.id.result_background).setImageBitmap(bitmap)
        findViewById<Button>(R.id.textrecog).setOnClickListener {

            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
            val result = detector.processImage(image)
                .addOnSuccessListener {
                    firebaseVisionText ->
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener{
                    findViewById<TextView>(R.id.resulttext).text = "Fail"
                }
        }
        findViewById<Button>(R.id.clouttextrecog).setOnClickListener {

            val image = com.google.firebase.ml.vision.common.FirebaseVisionImage.fromBitmap(bitmap)
            val detector = com.google.firebase.ml.vision.FirebaseVision.getInstance().cloudTextRecognizer
            val result = detector.processImage(image)
                .addOnSuccessListener {
                        firebaseVisionText ->
                    processResultText2(firebaseVisionText)
                }
                .addOnFailureListener{
                    findViewById<android.widget.TextView>(edu.skku2.map.lotto_kotlin.R.id.resulttext).text = "Fail"
                }
        }

    }
    private fun processResultText(resultText: FirebaseVisionText){
        if(resultText.textBlocks.size == 0){
            findViewById<TextView>(R.id.resulttext).text="no text"
            return
        }
        for(block in resultText.textBlocks){
            val blockText=block.text
            findViewById<TextView>(R.id.resulttext).append(blockText+"\n")
        }
    }
    private fun processResultText2(resultText: FirebaseVisionText){
        if(resultText.textBlocks.size == 0){
            findViewById<TextView>(R.id.resulttext).text="no text-cloud"
            return
        }
        for(block in resultText.textBlocks){
            val blockText=block.text
            findViewById<TextView>(R.id.resulttext).append(blockText+"\n")
        }
    }

}