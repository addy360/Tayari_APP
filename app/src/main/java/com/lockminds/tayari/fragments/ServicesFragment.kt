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
import com.lockminds.tayari.adapter.ServicesAdapter
import com.lockminds.tayari.data.Service
import com.lockminds.tayari.databinding.FragmentServicesBinding
import com.lockminds.tayari.viewModels.ServicesListViewModel
import com.lockminds.tayari.viewModels.ServicesListViewModelFactory

private const val BUSINESS_KEY = ""

class ServicesFragment() : Fragment() {

    private var busines_key: String? = null
    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private lateinit var linearLayoutManager: LinearLayoutManager


    val servicesListViewModel by viewModels<ServicesListViewModel>{
        ServicesListViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            busines_key = it.getString(BUSINESS_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val servicesAdapter = ServicesAdapter(requireActivity()) { service -> adapterOnClick(service) }

        linearLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = servicesAdapter

        servicesListViewModel.refreshList()
        servicesListViewModel.servicesLiveData.observe(requireActivity(), {
                        it?.let {
                servicesAdapter.submitList(it as MutableList<Service>)
            }
        })

        fetchServices()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        @JvmStatic
        fun newInstance(business_key: String) =
            ServicesFragment().apply {
                arguments = Bundle().apply {
                    putString(BUSINESS_KEY, business_key)
                }
            }
    }

    private fun adapterOnClick(service: Service) {
//        val intent = Intent(requireContext(), ServiceDetailsActivity()::class.java)
//        intent.putExtra("service_key", service.service_key)
//        startActivity(intent)
    }

    private fun fetchServices(){

        AndroidNetworking.get("https://api.tayari.co.tz/api/services")
                .addQueryParameter("business", busines_key)
            .setTag(this)
            .setPriority(Priority.LOW)
            .build()
            .getAsObjectList(Service::class.java, object : ParsedRequestListener<List<Service>> {
                override fun onResponse(service: List<Service>) {
                    val items: List<Service> = service

                    if(items.size > 0){
                        servicesListViewModel.insertServices(items);
                        binding.recyclerView.bringToFront()
                    }
                }

                override fun onError(anError: ANError) {

                }
            })
    }

}