package com.lockminds.tayari.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.lockminds.tayari.adapter.BusinessAdapter
import com.lockminds.tayari.data.Business
import com.lockminds.tayari.databinding.FragmentBusinessBinding
import com.lockminds.tayari.viewModels.BusinessListViewModel
import com.lockminds.tayari.viewModels.BusinessListViewModelFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BusinessFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentBusinessBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager

    val businessListViewModel by viewModels<BusinessListViewModel>{
        BusinessListViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBusinessBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val businessAdapter = BusinessAdapter(requireContext()) { business -> adapterOnClick(business) }

        linearLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = businessAdapter

        businessListViewModel.businessLiveData.observe(requireActivity(), {
            it?.let {
                businessAdapter.submitList(it as MutableList<Business>)
            }
        })

        fetchBusiness()
    }

    private fun fetchBusiness() {
        AndroidNetworking.get("https://api.tayari.co.tz/api/business/get_business")
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObjectList(Business::class.java, object : ParsedRequestListener<List<Business>> {
                override fun onResponse(business: List<Business>) {
                    val items: List<Business> = business
                    if (items.size > 0) {
                        businessListViewModel.insertBusinessList(items)
                        binding.recyclerView.bringToFront()
                    }
                }
                override fun onError(anError: ANError) {
                }
            })
    }

    private fun adapterOnClick(business: Business) {
//        val intent = Intent(requireContext(), NewsDetailsActivity()::class.java)
//        intent.putExtra(General.NEWS_KEY, business.business_key)
//        startActivity(intent)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BusinessFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}