package com.mohammedthowfiq.vehicledetection.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.adapter.HistoryAdapter
import com.mohammedthowfiq.vehicledetection.model.ImageModel

class HistoryFragment : Fragment() {


    lateinit var recyclerHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HistoryAdapter
    lateinit var progressBarLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences

    var imageUrlList = arrayListOf<ImageModel>()

    private var rootNode: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    private var emailAddress: String = ""
    var url:String? = ""
    var date:String? = ""
    var time:String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        sharedPreferences =
            (activity as Context).getSharedPreferences(
                getString(R.string.preferences_file),
                Context.MODE_PRIVATE
            )

        val fileEmailAddress = sharedPreferences.getString("Email Address", "defaultuser@gmail,com")

        emailAddress = fileEmailAddress.toString()


        recyclerHistory = view.findViewById(R.id.recyclerViewHistory)
        layoutManager = GridLayoutManager(activity as Context,2)


        progressBarLayout = view.findViewById(R.id.ProgressBarLayout)
        progressBar = view.findViewById(R.id.historyProgressBar)


        progressBarLayout.visibility = View.VISIBLE

        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode?.getReference("Uploads")

        val checkUser = reference?.child(emailAddress)
        checkUser?.addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                for(i in p0.children){
                    url = i.child("imageUrl").value.toString()
                    date = i.child("date").value.toString()
                    time = i.child("time").value.toString()



                    val imageModelObject = ImageModel(
                        url.toString(),
                        date.toString(),
                        time.toString()
                    )

                    imageUrlList.add(imageModelObject)

                }

                println(url)
                progressBarLayout.visibility = View.GONE
                recyclerAdapter = HistoryAdapter(activity as Context,imageUrlList)
                recyclerHistory.adapter = recyclerAdapter
                recyclerHistory.layoutManager = layoutManager

            }

            override fun onCancelled(p0: DatabaseError) {

                Toast.makeText(activity,"Something went wrong please try again after sometime",Toast.LENGTH_SHORT).show()


            }




        })









        return view
    }

}