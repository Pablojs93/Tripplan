package com.pjas.tripplan.Classes.Database.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pjas.tripplan.Classes.Database.Model.SharedWith
import com.pjas.tripplan.R

class TripSharedRecyclerViewAdapter(
    private val sharedWithList: MutableList<SharedWith>,
    private val context: Context)
: RecyclerView.Adapter<TripSharedRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripSharedRecyclerViewAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent!!.context).inflate(R.layout.sharedwith_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sharedWithList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sharedWith = sharedWithList[position]

        holder!!.email.text = sharedWith.email
        holder!!.name.text = sharedWith.name

        /*holder.details.setOnClickListener {
            getDetails(trip)
        }*/
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var email: TextView
        internal var name: TextView

        //internal var delete: ImageButton

        init {
            email = view.findViewById(R.id.tv_Email)
            name = view.findViewById(R.id.tv_Name)

            //delete = view.findViewById(R.id.b_Delete)
        }
    }
}