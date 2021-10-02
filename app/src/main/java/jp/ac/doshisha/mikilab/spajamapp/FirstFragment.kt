package jp.ac.doshisha.mikilab.spajamapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import jp.ac.doshisha.mikilab.spajamapp.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val dogs = listOf(
            Image(R.drawable.d1),
            Image(R.drawable.d2),
            Image(R.drawable.d3),
            Image(R.drawable.d4),
            Image(R.drawable.d5),
        )

        binding.cardTextView1.text = "dog"
        binding.cardGallery1.adapter = ImageAdapter(
            imageList = dogs,
            imageID = R.id.gallery_image_view,
            containerID = R.layout.gallery
        )
        binding.cardGallery1.layoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)

        val osakas = listOf(
            Image(R.drawable.o1),
            Image(R.drawable.o2),
            Image(R.drawable.o3),
            Image(R.drawable.o4),
            Image(R.drawable.o5),
        )

        binding.cardTextView2.text = "osaka"
        binding.cardGallery2.adapter = ImageAdapter(
            imageList = osakas,
            imageID = R.id.gallery_image_view,
            containerID = R.layout.gallery
        )
        binding.cardGallery2.layoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)


        val ramens = listOf(
            Image(R.drawable.r1),
            Image(R.drawable.r2),
            Image(R.drawable.r3),
            Image(R.drawable.r4),
            Image(R.drawable.r5),
        )

        binding.cardTextView3.text = "ramen"
        binding.cardGallery3.adapter = ImageAdapter(
            imageList = ramens,
            imageID = R.id.gallery_image_view,
            containerID = R.layout.gallery
        )
        binding.cardGallery3.layoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardTextView1.setOnClickListener {
            val b = Bundle()
            b.putString("selected", "dog")
            arguments = b
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, b)
        }

        binding.cardTextView2.setOnClickListener {
            val b = Bundle()
            b.putString("selected", "osaka")
            arguments = b
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, b)
        }

        binding.cardTextView3.setOnClickListener {
            val b = Bundle()
            b.putString("selected", "ramen")
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, b)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}