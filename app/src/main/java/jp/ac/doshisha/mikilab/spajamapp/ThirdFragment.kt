package jp.ac.doshisha.mikilab.spajamapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.*
import jp.ac.doshisha.mikilab.spajamapp.databinding.FragmentThirdBinding
import java.io.ByteArrayOutputStream


class ThirdFragment : Fragment()  {
    private var _binding: FragmentThirdBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var functions: FirebaseFunctions
    private var intentThrower: ActivityResultLauncher<Intent>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        intentThrower = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                it.data?.let { data: Intent ->
                    val uri = data.toUri(Intent.URI_INTENT_SCHEME)
                    Log.v(this.tag, uri)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val img : ImageView? = view?.findViewById(R.id.imgView)
        if (resultCode != AppCompatActivity.RESULT_OK || READ_REQUEST_CODE == requestCode) {
            val resultUri = data?.data
            val contentResolver = requireActivity().contentResolver
            val source = ImageDecoder.createSource(contentResolver, data?.data!!)
            var bitmap = ImageDecoder.decodeBitmap(source)
            // Scale down bitmap size
            bitmap = scaleBitmapDown(bitmap, 640)// Convert bitmap to base64 encoded string
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            val base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // Create json request to cloud vision
            val request = JsonObject()
            // Add image to request
            val image = JsonObject()
            image.add("content", JsonPrimitive(base64encoded))
            request.add("image", image)

            annotateImage(request.toString())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Task failed with an exception
                        val landmarkName = "No Landmark"
                        val confidence = 0
                        val NameText : TextView? = view?.findViewById(R.id.LandmarkName)
                        val ValConText : TextView? = view?.findViewById(R.id.Confidence)
                        NameText?.setText(landmarkName)
                        ValConText?.setText(confidence.toString())
                    } else {
                        // Task completed successfully
                        var landmarkName =""
                        var score = ""
                        for (label in task.result!!.asJsonArray[0].asJsonObject["landmarkAnnotations"].asJsonArray) {
                            val labelObj = label.asJsonObject
                            landmarkName = labelObj["description"].toString()
                            score = labelObj["score"].toString()
                        }

                        val NameText : TextView? = view?.findViewById(R.id.LandmarkName)
                        val ValConText : TextView? = view?.findViewById(R.id.Confidence)
                        NameText?.setText(landmarkName)
                        ValConText?.setText(score)

                    }
                }
//            //Add features to the request
//            val feature = JsonObject()
//            feature.add("maxResults", JsonPrimitive(5))
//            feature.add("type", JsonPrimitive("LANDMARK_DETECTION"))
//            val features = JsonArray()
//            features.add(feature)
//            request.add("features", features)
            if(null != resultUri)
            {
                if (img != null) {
                    img.setImageURI(resultUri)
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var nextbtn : Button = view.findViewById(R.id.button_select)
        nextbtn.background = resources.getDrawable(R.drawable.btn, null)

        nextbtn.setOnClickListener {
            selectPhoto()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}