package com.example.indianpincodes.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indianpincodes.R
import com.example.indianpincodes.adapter.ItemSetAdapter
import com.example.indianpincodes.databinding.FragmentSaveRecordBinding
import com.example.indianpincodes.model.Address
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SaveRecordFragment : Fragment() {

    private lateinit var binding:FragmentSaveRecordBinding
    private lateinit var adapter: ItemSetAdapter
    private var addresslist= mutableListOf<Address>()
    private lateinit var mRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSaveRecordBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRef= Firebase.database.reference
        adapter = ItemSetAdapter(requireContext(),addresslist, mRef,true){ itemId -> }


        binding.recycleview.layoutManager= LinearLayoutManager(context)
        binding.recycleview.adapter = adapter

        adapter.fetchAllLikeStatusFromFirebase()
        for (location in addresslist) {
            adapter.fetchLikeStatusFromFirebase(location)
        }

        adapter.notifyDataSetChanged()

    }




}