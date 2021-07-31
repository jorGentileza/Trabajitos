package com.trabajitos.apptrabajitos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trabajitos.apptrabajitos.databinding.ActivityHomeBinding
import java.util.jar.Manifest

class HomeActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mLastLocation: Location
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()


    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private lateinit var locationCallBack: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallBack,
                    Looper.myLooper()
                )
            }
        } else {
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallBack,
                Looper.myLooper()
            )
        }

    }

    /*
        Este metodo construye la solicitud de localizacion
     */
    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    /*
        Esta funcion construye  la respuesta a la geolocalizacion
     */
    private fun buildLocationCallback() {
        locationCallBack = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0!!.locations.get(p0!!.locations.size - 1)
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)

                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }
        }
    }

    /*
        Con esta funcion verificamos los permisos del usuario,
        de no tenerlos los solicita
     */
    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            return false
        } else
            return true
    }

    /*

     */
    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallBack,
                                Looper.myLooper()
                            )
                            mMap!!.isMyLocationEnabled = true
                        }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap!!.isMyLocationEnabled = true
            } else {
                mMap!!.isMyLocationEnabled = true
                mMap.uiSettings.isZoomControlsEnabled = true
            }
        }
        setUp()
    }


    private fun setUp() {
        mMap.setOnMarkerClickListener(this)

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val lat: Double = document.getString("lat")!!.toDouble()
                    val long: Double = document.getString("long")!!.toDouble()
                    val title = document.getString("title")
                    val id = document.id
                    val category = document.getString("category")
                    val position = LatLng(lat, long)

                    when (category) {
                        "belleza" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.belleza))
                                    .snippet(id)
                            )
                        }
                        "construccion" ->{
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.construccion))
                                    .snippet(id)
                            )
                        }
                        "educacion" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.educacion))
                                    .snippet(id)
                            )
                        }
                        "holistica" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.holistica))
                                    .snippet(id)
                            )
                        }
                        "jardineria" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.jardineria))
                                    .snippet(id)
                            )
                        }
                        "mascotas" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mascotas))
                                    .snippet(id)
                            )
                        }
                        "medicina" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.medicina))
                                    .snippet(id)
                            )
                        }
                        "moda" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.moda))
                                    .snippet(id)
                            )
                        }
                        "tecnologia" -> {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(position)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.tecnologia))
                                    .snippet(id)
                            )
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        binding.navigationBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_logout -> logout()
                R.id.action_profile -> showProfile()
                R.id.action_post -> postJob()
                R.id.action_messages -> myPosts()
            }
            true
        }

    }

    override fun onMarkerClick(p0: Marker): Boolean {

        val markerid = p0.snippet.toString()
        val intent = Intent(this, showJobActivity::class.java)
        intent.putExtra("id", markerid)
        startActivity(intent)
        finish()
        return false
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Seguro que deseas salir?")
            .setCancelable(false)
            .setPositiveButton("si"){ dialog,id ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
            .setNegativeButton("no"){dialog,id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun showProfile() {
        startActivity(Intent(this, MyProfileActivity::class.java))
        finish()
    }

    private fun postJob() {
        val intent = Intent(this, PostJobActivity::class.java)
        val latitud = latitude.toString()
        val longitud = longitude.toString()
        intent.putExtra("longitud", longitud)
        intent.putExtra("latitud", latitud)
        startActivity(intent)
        finish()
    }

    private fun myPosts() {
        startActivity(Intent(this, MyPostsActivity::class.java))
        finish()
    }
}