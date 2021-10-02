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
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
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
//        intentThrower = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it?.resultCode == Activity.RESULT_OK) {
//                it.data?.let { data: Intent ->
//                    val uri = data.toUri(Intent.URI_INTENT_SCHEME)
//                    Log.v(this.tag, uri)
//                    _uri = data.data
//                    val img : ImageView? = view?.findViewById(R.id.imgView)
//                    if (uri != null){
//                        img?.setImageURI(_uri)
//                    }
//                }
//            }
//        }
        val dogs = listOf(
            Image(R.drawable.d1),
            Image(R.drawable.d2),
            Image(R.drawable.d3),
            Image(R.drawable.d4),
            Image(R.drawable.d5),
            Image(R.drawable.d6),
            Image(R.drawable.d7),
            Image(R.drawable.d8),
            Image(R.drawable.d9),
            Image(R.drawable.d10),
            Image(R.drawable.d11),
            Image(R.drawable.d12),
            Image(R.drawable.d13),
            Image(R.drawable.d14),
            Image(R.drawable.d15),
        )
        val osakas = listOf(
            Image(R.drawable.o1),
            Image(R.drawable.o2),
            Image(R.drawable.o3),
            Image(R.drawable.o4),
            Image(R.drawable.o5),
            Image(R.drawable.o6),
            Image(R.drawable.o7),
            Image(R.drawable.o8),
            Image(R.drawable.o9),
            Image(R.drawable.o10),
            Image(R.drawable.o11),
            Image(R.drawable.o12),
            Image(R.drawable.o15),
            Image(R.drawable.o16),
            Image(R.drawable.o17),
            Image(R.drawable.o18),
        )

        val ramens = listOf(
            Image(R.drawable.r1),
            Image(R.drawable.r2),
            Image(R.drawable.r3),
            Image(R.drawable.r4),
            Image(R.drawable.r5),
            Image(R.drawable.r6),
            Image(R.drawable.r7),
            Image(R.drawable.r8),
            Image(R.drawable.r9),
            Image(R.drawable.r10),
            Image(R.drawable.r11),
            Image(R.drawable.r12),
            Image(R.drawable.r13),
            Image(R.drawable.r14),
            Image(R.drawable.r15),
        )

        val viewed_images = mutableListOf<Image>()
        viewed_images.addAll(osakas)
        viewed_images.addAll(ramens)
        viewed_images.addAll(dogs)

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
                Log.d("TAG", result.toString())
                JsonParser.parseString(Gson().toJson(result))
            }
    }

    private fun addlabel() {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val contentResolver = requireActivity().contentResolver
        val source = ImageDecoder.createSource(contentResolver, _uri!!)
        var bitmap = ImageDecoder.decodeBitmap(source)
        bitmap = scaleBitmapDown(bitmap, 640)// Convert bitmap to base64 encoded string
//      エンコードされた文字列に変換
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        val base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

        try{
            image = InputImage.fromFilePath(context, _uri)
        }catch (e: IOException){
            e.printStackTrace()
        }

        labeler.process(image)
            .addOnSuccessListener() { labels ->
                // Task completed successfully
                val resultText = StringBuilder()
                for (label in labels) {
                    val text = label.text
                    resultText.append(text)
                    resultText.append(" ")
                    val confidence = label.confidence
                    val index = label.index
                }
                val NameText : TextView? = view?.findViewById(R.id.RecText)
                NameText?.setText(resultText)

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                e.printStackTrace()
            }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nextbtn : Button = view.findViewById(R.id.button_select)
        val getlmbtn : Button = view.findViewById(R.id.landmarkrecog)

        nextbtn.setOnClickListener {
            selectPhoto()
        }
        getlmbtn.setOnClickListener {
            addlabel()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}