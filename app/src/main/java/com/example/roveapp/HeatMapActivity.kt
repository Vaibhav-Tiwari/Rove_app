package com.example.roveapp

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.roveapp.databinding.ActivityHeatMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.android.AndroidPlatform
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import org.json.JSONArray
import java.io.IOException


class HeatMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHeatMapBinding
    private lateinit var btn: FloatingActionButton
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var marker: Marker
    private lateinit var searchView: SearchView
    private lateinit var pieBtn : FloatingActionButton
    private lateinit var barBtn : FloatingActionButton

    private lateinit var spType : Spinner
    private lateinit var btFind : Button
    private lateinit var supportMapFragment : SupportMapFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHeatMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btn = binding.addButton
        pieBtn = binding.pieButton
        barBtn = binding.barButton
        //searchView = binding.idSearchView
//        if (! Python.isStarted()) {
//            Python.start( AndroidPlatform(this))
//        }
//        getPythonHelloWorld()

        barBtn.setOnClickListener{
            startActivity(Intent(this, BarChart::class.java))
        }
        pieBtn.setOnClickListener{
            startActivity(Intent(this, PieChart::class.java))
        }
        btn.setOnClickListener{
            val intent = Intent(this, ReportCrimeActivity::class.java)
            startActivity(intent)
        }

        spType = findViewById(R.id.sp_type)
        btFind = findViewById(R.id.bt_find)

//        var autotextView = findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
//        val languages = resources.getStringArray(R.array.Places)
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spType)
//        autotextView.setAdapter(adapter)

        val drawerLayout: DrawerLayout= findViewById(R.id.drawerLayout)
        val navView:NavigationView=findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        navView.setNavigationItemSelectedListener() {
            when(it.itemId){
                R.id.nav_home -> Toast.makeText(applicationContext,"Clicked Home",Toast.LENGTH_SHORT).show()
                R.id.duty_sch -> Toast.makeText(applicationContext,"Duty Schedule",Toast.LENGTH_SHORT).show()
                R.id.crime_identify -> startActivity(Intent(this, PieChart::class.java))
                R.id.crime_list -> Toast.makeText(applicationContext,"Crime List",Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(applicationContext,"Clicked Share",Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> logout()
            }

            true
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun logout() {
        Toast.makeText(applicationContext,"Clicked Logout",Toast.LENGTH_SHORT).show()


    }

//    private fun ArrayAdapter(heatMapActivity: HeatMapActivity, simpleSpinnerDropdownItem: Int, spType: Spinner?):  {
//
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    private fun getJsonDataFromAsset(fileName: String): JSONArray? {
        try {
            val jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
            return JSONArray(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    private fun generateHeatMapData(): ArrayList<WeightedLatLng> {
        val data = ArrayList<WeightedLatLng>()

        // call our function which gets json data from our asset file
        val jsonData = getJsonDataFromAsset("jipi8-scuu8.json")

        // ensure null safety with let call
        jsonData?.let {
            // loop over each json object
            for (i in 0 until it.length()) {
                // parse each json object
                val entry = it.getJSONObject(i)
                val lat = entry.getDouble("Latitude")
                val lon = entry.getDouble("Longitude")
                val weightedLatLng = WeightedLatLng(LatLng(lat, lon))
                data.add(weightedLatLng)
            }
        }

        return data
    }
//    private fun getPythonHelloWorld(): String {
//        val python = Python.getInstance()
//        val pythonFile = python.getModule("visual")
//        return pythonFile.callAttr("new_frame").toString()
//    }
    fun heat(googleMap: GoogleMap){
        val data = generateHeatMapData()
        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(data) // load our weighted data
            .radius(50) // optional, in pixels, can be anything between 20 and 50
            .build()
        googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
    }
    override fun onMapReady(googleMap: GoogleMap) {
        val data = generateHeatMapData()
        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(data) // load our weighted data
            .radius(50) // optional, in pixels, can be anything between 20 and 50
            .build()
        googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
        val indiaLatLng = LatLng(26.8467, 80.9462)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, 12f))
        /*googleMap.addMarker(
            MarkerOptions()
                .position(indiaLatLng)
                .title("Lucknow")
                .snippet("Latitude: 26.8467, Longitude: 80.9462"))*/
        googleMap.setOnMapClickListener { latLng -> // Creating a marker
            val markerOptions = MarkerOptions()

            // Setting the position for the marker
            markerOptions.position(latLng)

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)

            // Clears the previously touched position
            googleMap.clear()
            heat(googleMap)
            // Placing a marker on the touched position

            //heat(googleMap)
            // Animating to the touched position
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))


            googleMap.addMarker(markerOptions)
        }
    }
}