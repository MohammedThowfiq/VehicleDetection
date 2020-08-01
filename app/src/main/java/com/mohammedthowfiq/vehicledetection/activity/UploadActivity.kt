package com.mohammedthowfiq.vehicledetection.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mohammedthowfiq.vehicledetection.R
import com.mohammedthowfiq.vehicledetection.adapter.HistoryAdapter
import com.mohammedthowfiq.vehicledetection.adapter.RetrievalAdapter
import com.mohammedthowfiq.vehicledetection.model.ImageModel
import com.mohammedthowfiq.vehicledetection.model.RetrievalModel
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UploadActivity : AppCompatActivity() {

    private lateinit var uploadToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var uploadButton: Button
    private lateinit var chooseButton: Button
    private lateinit var imageView: ImageView

    private lateinit var filePath: Uri
    private val PICK_IMAGE_REQUEST = 234
    private var storageReference: StorageReference? = null
    lateinit var sharedPreferences: SharedPreferences
    private var emailAddress: String = ""
    private var rootNode: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null
    private var imageReference: DatabaseReference? = null
    private var outJsonReference: DatabaseReference? = null

    private var currentTimeInMills: String = ""

    private var requestQueue: RequestQueue? = null

    private var plate_confList = ArrayList<String>()
    private var numberPlateList = ArrayList<String>()
    private var colorList = ArrayList<String>()
    private var makeModelList = ArrayList<String>()


    private var dataList = arrayListOf<RetrievalModel>()
    private var newDataList = arrayListOf<RetrievalModel>()

    private lateinit var recyclerRetrieval: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: RetrievalAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        sharedPreferences =
            getSharedPreferences(
                getString(R.string.preferences_file),
                Context.MODE_PRIVATE
            )


        uploadToolbar = findViewById(R.id.uploadToolbar)
        uploadButton = findViewById(R.id.btn_UploadImage)
        chooseButton = findViewById(R.id.btn_ChooseImage)
        imageView = findViewById(R.id.imageView)

        recyclerRetrieval = findViewById(R.id.recyclerViewRetrieval)
        layoutManager = LinearLayoutManager(applicationContext)


        //getting firebase storage reference
        storageReference = FirebaseStorage.getInstance().getReference("Images")

        setSupportActionBar(uploadToolbar)
        supportActionBar?.title = "Upload Details"

        val fileEmailAddress = sharedPreferences.getString("Email Address", "defaultuser@gmail,com")


        emailAddress = fileEmailAddress.toString()
        println("email Address $emailAddress")



        uploadButton.setOnClickListener(View.OnClickListener {

            uploadFile()

        })

        chooseButton.setOnClickListener(View.OnClickListener {

            showFileChooser()

        })


        rootNode = FirebaseDatabase.getInstance()
        imageReference = rootNode?.getReference("Uploads")
        val check = imageReference?.child(fileEmailAddress.toString())?.child(currentTimeInMills)

        check?.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                if (snapshot.exists()) {

                    val imageUrl: String = snapshot.child("outurl").value.toString()
                    //  println("OutputImage Url is $imageUrl")

                    Picasso.get().load(imageUrl).error(R.drawable.ic_dummy_image).into(imageView)

                    /*   for (i in snapshot.children) {

                           println(i)

                       }

                     */


                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {

                    val imageUrl: String = snapshot.child("outurl").value.toString()
                    //  println("OutputImage Url is $imageUrl")

                    Picasso.get().load(imageUrl).error(R.drawable.ic_dummy_image).into(imageView)


                }


            }


            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }


        })


        outJsonReference = rootNode?.getReference("Uploads")
        val checkOutJsonReference =
            outJsonReference?.child(fileEmailAddress.toString())?.child(currentTimeInMills)
                ?.child("outjson")

        checkOutJsonReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UploadActivity,
                    "Something went wrong please try again after sometime",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val imageName = snapshot.child("image_name").value.toString()
                val location = snapshot.child("location").value.toString()
                val timestamp = snapshot.child("timestamp").value.toString()

                println(imageName)
                println(location)
                println(timestamp)


                val detection = snapshot.child("detections")
                    colorList.clear()
                    makeModelList.clear()
                    numberPlateList.clear()
                    plate_confList.clear()


                var detection_length = snapshot.child("detections").childrenCount.toInt()

                for (i in 0 until detection_length) {

//                    colorList.clear()
//                    makeModelList.clear()
//                    numberPlateList.clear()
//                    plate_confList.clear()

                    if (colorList.isEmpty() && makeModelList.isEmpty() && numberPlateList.isEmpty() && plate_confList.isEmpty()) {
                        var detectionColorIterator = detection.child("$i").child("colors")
                        var detectionMakeIterator = detection.child("$i").child("make_model")
                        var detectionPlateIterator = detection.child("$i").child("plate")
                        var detectionPlateConfInterator = detection.child("$i").child("plate_conf")



                        for(a in 0 until  detectionColorIterator.childrenCount.toInt()){
                            colorList.add(detectionColorIterator.child("$a").value.toString())


                        }
                        for(b in 0 until  detectionMakeIterator.childrenCount.toInt()){
                            makeModelList.add(detectionMakeIterator.child("$b").value.toString())
                        }
                        for(c in 0 until  detectionPlateIterator.childrenCount.toInt()){
                            numberPlateList.add(detectionPlateIterator.child("$c").value.toString())
                        }
                        for(d in 0 until  detectionPlateConfInterator.childrenCount.toInt()){
                            plate_confList.add(detectionPlateConfInterator.child("$d").value.toString())
                        }

               /*
                        for (j in detectionColorIterator.children) {

                            //   println(j.value.toString())
                            colorList.add(j.value.toString())


                        }

                        for (k in detectionMakeIterator.children) {

                            //     println(k.value.toString())
                            makeModelList.add(k.value.toString())

                        }

                        for (x in detectionPlateIterator.children) {

                            //    println(x.value.toString())
                            numberPlateList.add(x.value.toString())

                        }

                        for (y in detectionPlateConfInterator.children) {

                            //     println(y.value.toString())
                            plate_confList.add(y.value.toString())

                        }
*/

                        val dataListObject =
                            RetrievalModel(
                                colorList.toString(),
                                makeModelList.toString(),
                                numberPlateList.toString(),
                                plate_confList.toString()
                            )

                        dataList.add(dataListObject)

                        colorList.clear()
                        makeModelList.clear()
                        numberPlateList.clear()
                        plate_confList.clear()


                        //    println(dataListObject)

                    }
                    else {

                        colorList.clear()
                        makeModelList.clear()
                        numberPlateList.clear()
                        plate_confList.clear()
                /*

                        var detectionColorIterator = detection.child("$i").child("colors")
                        var detectionMakeIterator = detection.child("$i").child("make_model")
                        var detectionPlateIterator = detection.child("$i").child("plate")
                        var detectionPlateConfInterator = detection.child("$i").child("plate_conf")

                        for (j in detectionColorIterator.children) {

                            //   println(j.value.toString())
                            colorList.add(j.value.toString())


                        }

                        for (k in detectionMakeIterator.children) {

                            //     println(k.value.toString())
                            makeModelList.add(k.value.toString())

                        }

                        for (x in detectionPlateIterator.children) {

                            //    println(x.value.toString())
                            numberPlateList.add(x.value.toString())

                        }

                        for (y in detectionPlateConfInterator.children) {

                            //     println(y.value.toString())
                            plate_confList.add(y.value.toString())

                        }

                        val dataListObject =
                            RetrievalModel(
                                colorList,
                                makeModelList,
                                numberPlateList,
                                plate_confList
                            )

                        dataList.add(dataListObject)

                           println(dataListObject)

      */

                    }
                 //  println(dataList)


                }
                println(dataList.size)

                println(" Data List $dataList")

                recyclerAdapter = RetrievalAdapter(applicationContext, dataList)
                recyclerRetrieval.adapter = recyclerAdapter
                recyclerRetrieval.layoutManager = layoutManager

            }


        })





    }


    //method to show file chooser
    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
    }

    //handling the image chooser activity result
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data!!
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //this method will upload the file
    private fun uploadFile() {
        //val imageName: String = ImageName.getText().toString()
        val currentDate: String = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        currentTimeInMills = System.currentTimeMillis().toString()

        val fileName = currentDate + "__" + currentTime

        val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Progress")
            progressDialog.show()
            val riversRef = storageReference!!.child(emailAddress).child("$fileName.jpg")
            riversRef.putFile(filePath)
                .addOnSuccessListener {
                    //if the upload is successful
                    //hiding the progress dialog
                    progressDialog.dismiss()

                    //and displaying a success toast
                    Toast.makeText(applicationContext, "File Uploaded ", Toast.LENGTH_LONG)
                        .show()


                }
                .addOnFailureListener { exception -> //if the upload is not successful
                    //hiding the progress dialog
                    progressDialog.dismiss()

                    //and displaying error message
                    Toast.makeText(
                        applicationContext,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnProgressListener { taskSnapshot -> //calculating progress percentage

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploading...")

                }

            val uploadTask = riversRef.putFile(filePath)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                riversRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    val imageModelObject = ImageModel(downloadUri.toString(), date, currentTime)
                    //  println(downloadUri.toString())

                    //            val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                    val properEmailAddress = emailAddress.replace('.', ',')
                    rootNode = FirebaseDatabase.getInstance()
                    reference = rootNode?.getReference("Uploads")?.child(properEmailAddress)
                    //        reference = rootNode?.getReference("Users")?.child("Uploads")

                    //  reference?.child(currentDate)?.child(currentTime)?.setValue(imageModelObject)

                    reference?.child(currentTimeInMills)?.setValue(imageModelObject)

                    reference = rootNode?.getReference("Uploads")?.child("ToBepProcessed")

                    val toBeProcessedObject = HashMap<String, String>()
                    toBeProcessedObject["imageName"] = currentTimeInMills

                    reference?.setValue(toBeProcessedObject)

                } else {
                    // Handle failures
                    println("Sorry url can't be get!")
                    // ...
                }


                //       imageReference = rootNode?.getReference("OutputImages")


                //            val check = imageReference?.child(emailAddress.replace('.', ','))
                /*           check?.addChildEventListener(object : ChildEventListener {

                                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                                    val imageUrl = p0.child("imageUrl").value.toString()
                                    println("output image url $imageUrl")


                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                                    TODO("Not yet implemented")
                                }

                                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                                    TODO("Not yet implemented")
                                }

                                override fun onChildRemoved(p0: DataSnapshot) {
                                    TODO("Not yet implemented")
                                }


                            })


                            */
            }


        } else {
            //you can display an error toast
            Toast.makeText(
                this@UploadActivity,
                "Please select a image to upload",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_drawer, menu)



        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.history -> {

                startActivity(Intent(this@UploadActivity, TransitionActivity::class.java))

            }

        }





        return super.onOptionsItemSelected(item)
    }


}