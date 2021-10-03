package jp.ac.doshisha.mikilab.spajamapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.functions.FirebaseFunctions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import jp.ac.doshisha.mikilab.spajamapp.databinding.FragmentThirdBinding
import java.io.File
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
    private var viewed_images_name = mutableListOf<String>()
    private var viewed_images = mutableListOf<Uri>()
    private var _labels = mutableListOf<String>()
    private var listoflabels = mutableListOf<String>()
    private var osaka_list = mutableListOf<String>()
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private var path_list = mutableListOf<String>()
    private var dictoflabel = mutableMapOf<String, List<String>>()
    private var index = 0
    private var dog_img_list = mutableListOf<String>()
    private var ramen_img_list = mutableListOf<String>()
    private var osaka_img_list = mutableListOf<String>()


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
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d1"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d2"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d3"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d4"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d5"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d6"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d7"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d8"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d9"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d10"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d11"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d13"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d14"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/d15"),
        )

        val osakas = listOf(
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o1"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o2"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o3"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o4"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o5"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o6"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o7"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o8"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o9"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o10"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o11"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o12"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o15"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o16"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o17"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/o18"),
        )

        val ramens = listOf(
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r1"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r2"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r3"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r4"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r5"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r6"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r7"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r8"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r9"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r10"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r11"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r12"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r13"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r14"),
            Uri.parse("android.resource://jp.ac.doshisha.mikilab.spajamapp/drawable/r15"),
        )
        val dogs_name = listOf(
            ("d1"),
            ("d2"),
            ("d3"),
            ("d4"),
            ("d5"),
            ("d6"),
            ("d7"),
            ("d8"),
            ("d9"),
            ("d10"),
            ("d11"),
            ("d14"),
            ("d15"),
        )

        val osakas_name = listOf(
            ("o1"),
            ("o2"),
            ("o3"),
            ("o4"),
            ("o5"),
            ("o6"),
            ("o7"),
            ("o8"),
            ("o9"),
            ("o10"),
            ("o11"),
            ("o12"),
            ("o15"),
            ("o16"),
            ("o17"),
            ("o18"),
        )

        val ramens_name = listOf(
            ("r1"),
            ("r2"),
            ("r3"),
            ("r4"),
            ("r5"),
            ("r6"),
            ("r7"),
            ("r8"),
            ("r9"),
            ("r10"),
            ("r11"),
            ("r12"),
            ("r13"),
            ("r14"),
            ("r15"),
        )

        viewed_images.addAll(osakas)
        viewed_images.addAll(ramens)
        viewed_images.addAll(dogs)
        viewed_images_name.addAll(osakas_name)
        viewed_images_name.addAll(ramens_name)
        viewed_images_name.addAll(dogs_name)
        osaka_list.addAll(osakas_name)

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

    private fun labelUri(){
        try{
            image = com.google.mlkit.vision.common.InputImage.fromFilePath(context, _uri)
        }catch (e: IOException){
            e.printStackTrace()
        }

        labeler.process(image)
            .addOnSuccessListener() { labels ->
                // Task completed successfully
                var resultText : String = ""
                for (label in labels) {
                    resultText = label.text
                    break
                }
                val NameText : TextView? = view?.findViewById(jp.ac.doshisha.mikilab.spajamapp.R.id.RecText)
                NameText?.setText(resultText)
                _labels.add(resultText)


                if(resultText == "Dog"){
                    var imagepath ="R.drawable"+viewed_images_name.get(index)
                    dog_img_list.add(imagepath)
                }
                else if(resultText == "Food"){
                    var imagepath ="R.drawable"+viewed_images_name.get(index)
                    ramen_img_list.add(imagepath)
                }
                else{
                    var imagepath ="R.drawable"+viewed_images_name.get(index)
                    osaka_img_list.add(imagepath)
                }
                index = index +1
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
                e.printStackTrace()
            }
    }

    private fun addlabel() {
        for(img in viewed_images) {
            _uri = img
            labelUri()
        }

    }

    private fun getexif(){
        for(o in osaka_list){
            var tmp = resources.assets.open(o + ".jpeg")
            var exifInterface = ExifInterface(tmp)
            var result1 = exifInterface.getAttribute(TAG_GPS_LONGITUDE)
            var result2 = exifInterface.getAttribute(TAG_GPS_LATITUDE)
            Log.d("LATITUDE", result1.toString())
            Log.d("LATITUDE", result2.toString())
        }
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val nextbtn : Button = view.findViewById(R.id.button_select)
//        val getlmbtn : Button = view.findViewById(R.id.landmarkrecog)

//        nextbtn.setOnClickListener {
//            var index = 0
//            var imagepath = ""

//            for(label in _labels){
//                Log.d("NAME", label)
//                Log.d("LABEL", viewed_images_name.get(index))
//                path_list.add(imagepath)
//                index = index +1
//
//            }
//            Log.d("NAME", "Dog")
//            Log.d("IMG", dog_img_list.toString())
//
//            Log.d("NAME", "Ramen")
//            Log.d("IMG", ramen_img_list.toString())
//
//            Log.d("NAME", "Osaka")
//            Log.d("IMG", osaka_img_list.toString())
//
//            getexif()
//        }
//        getlmbtn.setOnClickListener {
//            addlabel()
//        }
//        addlabel()
//        Thread.sleep(3000)
//
        Handler(Looper.getMainLooper()).postDelayed( {
            findNavController().navigate(R.id.action_thirdFragment_to_FirstFragment)
        },3600)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun <E> MutableList<E>.addAll(elements: List<Uri>) {

}
