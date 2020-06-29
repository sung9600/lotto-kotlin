package edu.skku2.map.lotto_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val REQUEST_CODE_PERMISSIONS =10
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
private val REQUIRED_PERMISSIONS= arrayOf(android.Manifest.permission.CAMERA)
class MainActivity : AppCompatActivity() {
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var outputDirectory:File
    private lateinit var cameraExecutor:ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(allPermissionsGranted()){
            startCamera()
        }
        else{
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        outputDirectory = getOutputDirectory(this)
        button.setOnClickListener{
            takePicture()
        }
    }

    private fun takePicture(){
        val file= createFile(
            outputDirectory,
            FILENAME_FORMAT,
            PHOTO_EXTENSION
        )
        val outputFileOptions= ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture?.takePicture(outputFileOptions,executor,object:ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                //Toast.makeText(this@MainActivity, "Asdf", Toast.LENGTH_LONG).show() 
            }

            override fun onError(exception: ImageCaptureException) {
                //Toast.makeText(this@MainActivity, "asdf", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun startCamera(){

        val cameraProviderFuture=ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider:ProcessCameraProvider=cameraProviderFuture.get()
            preview=Preview.Builder().build()
            imageCapture=ImageCapture.Builder().build()
            val cameraSelector=CameraSelector
                .Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            try{
                cameraProvider.unbindAll()
                camera=cameraProvider
                    .bindToLifecycle(this,cameraSelector,preview,imageCapture)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            }catch (exc:Exception){
                Log.e("Fail", exc.toString())
            }
        },ContextCompat.getMainExecutor(this))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode== REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera()
            }
            else{
                Toast.makeText(this,"notgranted",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PHOTO_EXTENSION=".jpg"

        fun getOutputDirectory(context: Context): File{
            val appContext= context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())mediaDir else appContext.filesDir
        }
        fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)
    }

}
