package jp.ac.doshisha.mikilab.spajamapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.eclipsesource.json.Json

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [postFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
object OkHttp {
    private val Json: MediaType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    fun buildRequestBody(body: String, mediaType: MediaType = Json): RequestBody {
        return body.toRequestBody(mediaType)
    }

    fun buildRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .build()
    }

    fun buildPostRequest(url: String, requestBody: RequestBody): Request {
        return Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
    }

    fun execute(request: Request): String? {
        val response = client.newCall(request).execute()
        return response.body?.string()
    }
}

class HttpClient {
    fun get(url: String): String? {
        val request = OkHttp.buildRequest(url)
        return OkHttp.execute(request)
    }
    fun post(url: String, body: String): String? {
        val requestBody = OkHttp.buildRequestBody(body)
        val request = OkHttp.buildPostRequest(url, requestBody)
        return OkHttp.execute(request)
    }
}

class postFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name: EditText = view.findViewById(R.id.editName)
        val textbox : TextView = view.findViewById(R.id.textView)
        var sendbtn : Button = view.findViewById(R.id.LoginButton)
        val body:FormBody = FormBody.Builder()
            .add("name", name.toString())
            .build()
        val URL:String = "https://qiita.com/api/v2/items/bf3e4e06022eebe8e3eb"

        sendbtn.setOnClickListener{
            // 非同期処理
            fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main){
            // 関数実行
                val http = HttpClient()
                async(Dispatchers.Default) { http.get(URL) }.await().let {
                    val result = Json.parse(it).asObject()
                    textbox.setText(result.get("likes_count").asInt().toString() + "LGTM!")
                }
//                async(Dispatchers.Default) { http.post(URL, body.toString()) }.await().let {
//                    val result = Json.parse(it).asObject()
//                    textbox.setText(result.get("likes_count").asInt().toString() + "LGTM!")
//                }
            }
            onParallelGetButtonClick()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment postFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                postFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}