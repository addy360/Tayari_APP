package com.lockminds.tayari.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ybq.android.spinkit.SpinKitView
import com.lockminds.tayari.App
import com.lockminds.tayari.MenuActivity
import com.lockminds.tayari.R
import com.lockminds.tayari.adapter.MenuAdapter
import com.lockminds.tayari.databinding.FragmentRestaurantTabsBinding
import com.lockminds.tayari.model.Menu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val DATE_KEY = "data_key"


/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantTabsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantTabsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var data_key: String? = null
    private var spinKitView: SpinKitView? = null
    private var _binding: FragmentRestaurantTabsBinding? = null
    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data_key = it.getString(DATE_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRestaurantTabsBinding.inflate(inflater, container, false)
        val view = binding?.root

        binding?.recyclerView?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerView?.setHasFixedSize(true)
        setAdapter()
        spinKitView = view?.findViewById(R.id.spin_kit)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAdapter() {
//        binding?.spinKit?.isVisible = true
        val adapter = MenuAdapter(requireContext()){ offer -> adapterOnClick(offer) }
        GlobalScope.launch {
            val list = (activity?.application as App).repository.cousinMenu(data_key.toString())
            activity?.runOnUiThread(Runnable {
                //on main thread
                binding?.recyclerView?.adapter = adapter
                adapter.submitList(list)
            })
        }
    }

    private fun adapterOnClick(obj: Menu) {
        startActivity(MenuActivity.createMenuIntent(requireContext(), obj))
    }

    companion object {

        @JvmStatic fun newInstance(param1: String) =
                RestaurantTabsFragment().apply {
                    arguments = Bundle().apply {
                        putString(DATE_KEY, param1)
                    }
                }
    }
}