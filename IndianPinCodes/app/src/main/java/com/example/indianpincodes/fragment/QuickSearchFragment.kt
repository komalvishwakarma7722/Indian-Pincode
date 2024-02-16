package com.example.indianpincodes.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indianpincodes.adapter.ItemSetAdapter
import com.example.indianpincodes.databinding.FragmentQuickSearchBinding
import com.example.indianpincodes.model.Address
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class QuickSearchFragment : Fragment() {
    private lateinit var binding: FragmentQuickSearchBinding
    private lateinit var adapter: ItemSetAdapter
    private var addresslist = mutableListOf<Address>()
    private lateinit var mRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQuickSearchBinding.inflate(inflater, container, false)

        binding = FragmentQuickSearchBinding.inflate(inflater, container, false)
        mRef = Firebase.database.reference
        adapter = ItemSetAdapter(requireContext(), addresslist,mRef, false){ itemId ->

        }
        binding.recycleview.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleview.adapter = adapter

        binding.recycleview1.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleview1.adapter = adapter

        binding.recycleview2.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleview2.adapter = adapter
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRef = Firebase.database.reference

        binding.radioButtonOption1.setOnClickListener { onRadioButtonClick(binding.radioButtonOption1) }
        binding.radioButtonOption2.setOnClickListener { onRadioButtonClick(binding.radioButtonOption2) }
        binding.radioButtonOption3.setOnClickListener { onRadioButtonClick(binding.radioButtonOption3) }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterDataByPin(it) }
                return true
            }

        })
        binding.searchView1.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterDataByDistrict(it) }

                return true
            }
        })
        binding.searchView2.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    filterDataByOfficeName(it)
                }
                return true
            }

        })


    }

    private fun filterDataByOfficeName(officename: String) {
        addresslist.clear()
        mRef.child("address").orderByChild("postOfficeName").equalTo(officename).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val location = snapshot.getValue(Address::class.java)
                    location?.let {
                        addresslist.add(it)
                    }
                }
                adapter.updateData(addresslist, true)
                adapter.notifyDataSetChanged()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "onCancelled: ${databaseError.message}")
            }
        })

    }

    private fun filterDataByDistrict(district: String) {
        addresslist.clear()
        mRef.child("address").orderByChild("district").startAt(district).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val location = snapshot.getValue(Address::class.java)
                    location?.let {
                        addresslist.add(it)
                    }
                }
                adapter.updateData(addresslist, true)
                adapter.notifyDataSetChanged()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "onCancelled: ${databaseError.message}")
            }
        })
    }

    private fun filterDataByPin(pin: String) {
        // If the search query is empty, show the original data
        addresslist.clear()
        pin.length>=2
        mRef.child("address").orderByChild("pincode").startAt(pin).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val location = snapshot.getValue(Address::class.java)
                    location?.let {
                        addresslist.add(it)
                    }
                }
                adapter.updateData(addresslist, true)
                adapter.notifyDataSetChanged()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "onCancelled: ${databaseError.message}")
            }
        })
    }

    private fun onRadioButtonClick(radioButton:View) {
        when(radioButton.id){
            binding.radioButtonOption1.id  -> {
                binding.searchView.visibility = View.VISIBLE
                binding.searchView1.visibility = View.GONE
                binding.recycleview.visibility= View.VISIBLE
                binding.recycleview1.visibility= View.GONE
                binding.searchView2.visibility=View.GONE
                binding.recycleview2.visibility=View.GONE

            }
            binding.radioButtonOption2.id -> {
                // Show/hide views based on the selected radio button
                binding.searchView1.visibility = View.VISIBLE
                binding.searchView.visibility= View.GONE
                binding.recycleview1.visibility= View.VISIBLE
                binding.recycleview.visibility= View.GONE
                binding.searchView2.visibility= View.GONE
                binding.recycleview2.visibility=View.GONE
            }
            binding.radioButtonOption3.id->{
                binding.searchView1.visibility = View.GONE
                binding.searchView.visibility= View.GONE
                binding.recycleview1.visibility= View.GONE
                binding.recycleview.visibility= View.GONE
                binding.searchView2.visibility= View.VISIBLE
                binding.recycleview2.visibility=View.VISIBLE
            }
        }

    }

    private fun ItemSetAdapter.updateData(mutableList: List<Address>,displayData: Boolean = true) {
        this.addresslist = mutableList.toMutableList()
        this.displayData=displayData
        for (location in mutableList) {
            fetchLikeStatusFromFirebase(location)
        }

        notifyDataSetChanged()
    }



}