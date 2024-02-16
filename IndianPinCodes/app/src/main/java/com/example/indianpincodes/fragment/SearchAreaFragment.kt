package com.example.indianpincodes.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indianpincodes.adapter.ItemSetAdapter
import com.example.indianpincodes.databinding.FragmentSearchAreaBinding
import com.example.indianpincodes.model.Address
import com.example.indianpincodes.model.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchAreaFragment : Fragment() {

    private lateinit var binding: FragmentSearchAreaBinding
    private lateinit var mRef: DatabaseReference
    var stateList = mutableListOf<State>()
    private lateinit var mAdapter: ItemSetAdapter
    private var addressList = mutableListOf<Address>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchAreaBinding.inflate(inflater, container, false)

        mRef = Firebase.database.reference
        mAdapter = ItemSetAdapter(requireContext(), addressList,mRef, false){ itemId ->
        }
        binding.recycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleview.adapter = mAdapter
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRef.child("state").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                stateList.clear()

                for (snap in snapshot.children) {
                    val state = snap.getValue(State::class.java)
                    state?.let { stateList.add(it) }
                }
                var adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stateList)
                binding.autoState.setAdapter(adapter)

                binding.autoState.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        var state = stateList[position]
                        Log.d("SelectedState", state.toString())
                        loadDistrictList(state)

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding.search.setOnClickListener {
            var selectedDistrict = binding.autoDistrict.selectedItem.toString()
            var selectedCity = binding.autoCity.selectedItem.toString()
            var filterList =
                addressList.filter { it.district == selectedDistrict || it.city == selectedCity }
            mAdapter.updateData(filterList,true)
        }
        binding.clear.setOnClickListener {
            binding.autoDistrict.setSelection(0)
            binding.autoState.setSelection(0)
            binding.autoCity.setSelection(0)


            // Clear adapter data
            mAdapter.updateData(emptyList(), false)
        }
    }

    private fun loadDistrictList(state: State) {

        var districts = state.districts as List<String>

        var disAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            districts
        )
        binding.autoDistrict.setAdapter(disAdapter)

        binding.autoDistrict.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var district = districts[position]
                loadCity(district)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun loadCity(district: String) {
        addressList.clear()
        mRef.child("address").orderByChild("district").equalTo(district).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val uniqueCities:MutableSet<String> = mutableSetOf<String>()
                    for(snap in snapshot.children){
                        var address = snap.getValue(Address::class.java)
                        address?.let {
                            if (uniqueCities.add(it.city!!))
                                addressList.add(it)
                        }
                    }

                    var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, addressList.map { it.city })
                    binding.autoCity.adapter = adapter

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TAG", "onCancelled: ")
                }

            })

    }


}

