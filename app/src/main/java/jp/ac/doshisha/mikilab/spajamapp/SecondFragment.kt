package jp.ac.doshisha.mikilab.spajamapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import jp.ac.doshisha.mikilab.spajamapp.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
//        viewed_images.addAll(ramens)
//        viewed_images.addAll(dogs)

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = ImageAdapter(
            imageList = viewed_images,
            imageID = R.id.internal_image_view,
            containerID = R.layout.image_item
        )
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL
            )

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textviewSecond.text = "Osaka"

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}