package com.example.indianpincodes.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.indianpincodes.R
import com.example.indianpincodes.databinding.ItemSetLayoutBinding
import com.example.indianpincodes.model.Address
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ItemSetAdapter(var context:Context,
                     var addresslist:MutableList<Address>,var mRef:DatabaseReference,var displayData:Boolean=false,private val OnItemClick:(Int)-> Unit)
    :RecyclerView.Adapter<ItemSetAdapter.MyViewHolder>() {
    class MyViewHolder (var binding:ItemSetLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var binding=ItemSetLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return if (displayData) addresslist.size else 0
//       return addresslistlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var address=addresslist[position]
        holder.binding.pincode.text=address.pincode
        holder.binding.officename.text=("${address.postOfficeName}")
        holder.binding.statename.text=("${address.district},${address.state}")
        address.city

        if (address.isLike){
           holder. binding.like.setImageResource(R.drawable.icon_like_fill)
           holder. binding.like.imageTintList = ColorStateList.valueOf(Color.parseColor("#d6493e"))
        }

        // Set up click listeners for like and share
        holder.binding.like.setImageResource(
            if (address.isLike) R.drawable.icon_like_fill
            else R.drawable.baseline_favorite_border_24
        )
        holder.binding.like.setOnClickListener {
            address.isLike = !address.isLike
            updateLikeStatusInFirebase(address)
            notifyItemChanged(position)
        }

        holder.binding.share.setOnClickListener {
            shareLocation(address)
        }
        holder.binding.cardview.setOnClickListener {
            OnItemClick(address.pincode!!.toInt())
            showFormWithData(holder,address.pincode!!.toInt())
        }

    }

    private fun shareLocation(address: Address) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        // Customize the text you want to share
        val shareText = "Location: ${address.postOfficeName}, ${address.district}, ${address.state},${address.pincode}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        // Start the activity to show the share dialog
        context.startActivity(Intent.createChooser(shareIntent, "Share Location"))
    }

    private fun updateLikeStatusInFirebase(address: Address) {
        var pincodeNodeRef = mRef.child("likeStatus").child(address.pincode.toString())
        if (address.isLike) {
            // Update the like status for the specific location in Firebase
            val likeData = mapOf(
                "isLike" to true,
                "district" to address.district,
                "postOfficeName" to address.postOfficeName,
                "state" to address.state,
                "city" to address.city
            )

            pincodeNodeRef.setValue(likeData)
                .addOnSuccessListener {
                    // Successfully updated like status in Firebase
                    Log.d("Firebase", "Like status updated successfully")
                }
                .addOnFailureListener { e ->
                    // Failed to update like status in Firebase
                    Log.e("Firebase", "Error updating like status", e)
                }
        }else {
            // If isLike is false, remove the entry from the "likeStatus" node
            pincodeNodeRef.removeValue()
                .addOnSuccessListener {
                    // Successfully removed the entry
                    Log.d("Firebase", "Entry removed successfully")
                }
                .addOnFailureListener { e ->
                    // Failed to remove the entry
                    Log.e("Firebase", "Error removing entry", e)
                }
        }

    }

    fun updateData(newList: List<Address>,displayData: Boolean=true) {
        this.addresslist.clear()
        this.addresslist.addAll(newList)
        this.displayData=displayData
        notifyDataSetChanged()
    }
    fun fetchLikeStatusFromFirebase(location: Address) {
        val pincodeNodeRef = mRef.child("likeStatus").child(location.pincode.toString())

        // Fetch the liked status from Firebase
        pincodeNodeRef.child("isLike").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isLike = dataSnapshot.getValue(Boolean::class.java) ?: false
                location.isLike = isLike
                notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching like status", databaseError.toException())
            }
        })
    }

    fun fetchAllLikeStatusFromFirebase() {
        addresslist.clear()
        // Reference to the "likeStatus" node
        val likeStatusNodeRef = mRef.child("likeStatus")
        // Fetch all like status data from Firebase
        likeStatusNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val likeStatusList = mutableListOf<Address>()
                for (snapshot in dataSnapshot.children) {
                    val pincode = snapshot.key.toString()
                    val isLike = snapshot.child("isLike").getValue(Boolean::class.java) ?: false
                    val district = snapshot.child("district").getValue(String::class.java) ?: ""
                    val state = snapshot.child("state").getValue(String::class.java) ?: ""
                    val city = snapshot.child("city").getValue(String::class.java) ?: ""
                    val officename =
                        snapshot.child("postOfficeName").getValue(String::class.java) ?: ""
                    if (isLike) {
                        // Create a Location object and add it to the list
                        val location = Address(
                            pincode = pincode,
                            isLike = isLike,
                            city=city,
                            district = district,
                            state = state,
                            postOfficeName = officename
                        )
                        likeStatusList.add(location)
                    } else {
                        // If isLike is false, delete the entry from the "likeStatus" node
                        snapshot.ref.removeValue()
                    }
                }
                updateData(likeStatusList)
                notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching all like status", databaseError.toException())
            }
        })
    }

    private fun showFormWithData(holder: MyViewHolder,itemId: Int) {
        // Implement logic to update form elements based on the item ID
        val selectedLocation = addresslist.find { it.pincode?.toInt() == itemId }
        selectedLocation?.let {
            holder.binding.officename1.text =("Office Name:   ${it.postOfficeName}")
            holder.binding.pincode1.text =("Pincode:          ${it.pincode}")
            holder.binding.city.text= ("City:                  ${it.city}")
            holder.binding.district.text =("District:            ${it.district}")
            holder.binding.state.text =("State:               ${it.state}")

            if (holder.binding.addressform.isVisible) {
                holder.binding.addressform.visibility = View.GONE
            } else {
                holder.binding.addressform.visibility = View.VISIBLE
            }
        }
    }
}