package jp.ac.doshisha.mikilab.spajamapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import jp.ac.doshisha.mikilab.spajamapp.databinding.FragmentThirdBinding
import java.io.ByteArrayOutputStream
import java.io.IOException


class ThirdFragment : Fragment()  {
    private var _binding: FragmentThirdBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var functions: FirebaseFunctions
    private var intentThrower: ActivityResultLauncher<Intent>? = null
    private var _uri: Uri? = null
    private var  image: InputImage? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        intentThrower = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                it.data?.let { data: Intent ->
                    val uri = data.toUri(Intent.URI_INTENT_SCHEME)
                    Log.v(this.tag, uri)
                    _uri = data.data
                    val img : ImageView? = view?.findViewById(R.id.imgView)
                    if (uri != null){
                        img?.setImageURI(_uri)
                    }

                }
            }
        }

        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root

    }

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        intentThrower?.launch(intent)
    }
    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth =
                (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension
            resizedHeight =
                (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }

    private fun annotateImage(requestJson: String): Task<JsonElement> {
        return functions
            .getHttpsCallable("annotateImage")
            .call(requestJson)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data
                JsonParser.parseString(Gson().toJson(result))
            }
    }
    private fun getlandmark() {
//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        val contentResolver = requireActivity().contentResolver
        val source = ImageDecoder.createSource(contentResolver, _uri!!)
        var bitmap = ImageDecoder.decodeBitmap(source)
        bitmap = scaleBitmapDown(bitmap, 640)// Convert bitmap to base64 encoded string
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        try{
            image = InputImage.fromFilePath(context, _uri)
        }catch (e: IOException){
            e.printStackTrace()
        }
        val result = recognizer.process(image)
            .addOnSuccessListener() { visionText ->
                // Task completed successfully
                val resultText = visionText.text
                val NameText : TextView? = view?.findViewById(R.id.RecText)
                NameText?.setText(resultText)
                for (block in visionText.textBlocks) {
                    val blockText = block.text

                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextbtn : Button = view.findViewById(R.id.button_select)
        val getlmbtn : Button = view.findViewById(R.id.landmarkrecog)
        nextbtn.background = resources.getDrawable(R.drawable.btn, null)

        nextbtn.setOnClickListener {
            selectPhoto()
        }
        getlmbtn.setOnClickListener {
            getlandmark()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}