package com.projekt401.locationservicetest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


private const val PERMISSION_REQUEST = 10
private const val LOCATION_STATUS = "CoreAndroidLocation: "
private const val SERVICIO = "Servicio: "

class MainActivity : AppCompatActivity() {


    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)


    // onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disableView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }


        val input: String = "Has iniciado sesion"
        val serviceIntent = Intent(this, ExampleService::class.java)
        serviceIntent.putExtra("inputExtra", input)

        button_start.setOnClickListener {

            // Aqui se activa el servicio
            // Aqui se le deberia pasar la ubicacion y que se mantenga en ejecucion (background)

            startService(serviceIntent)
            Toast.makeText(this, "El servicio ha iniciado", Toast.LENGTH_SHORT).show()
        }


        button_stop.setOnClickListener {

            // Este seria el switch para apagar el servicio (Cuando el conductor ya finaliza sesion)
            // O manualmente desactiva el servicio de recibir viajes

            stopService(serviceIntent)
            Toast.makeText(this, "El servicio se ha detenido", Toast.LENGTH_SHORT).show()
        }

    }

    // Desabilitar boton
    private fun disableView() {
        button_ubicacion.isEnabled = false
        button_ubicacion.alpha = 0.5F
    }

    // Activar boton
    private fun enableView() {
        button_ubicacion.isEnabled = true
        button_ubicacion.alpha = 1F
        button_ubicacion.setOnClickListener { getLocation()}
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
    }


    // Obtener la ubicacion
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            textView_resultado.append("\nGPS ")
                            textView_resultado.append("\nLatitude : " + locationGps!!.latitude)
                            textView_resultado.append("\nLongitude : " + locationGps!!.longitude)
                            Log.d(LOCATION_STATUS, " GPS Latitude : " + locationGps!!.latitude)
                            Log.d(LOCATION_STATUS, " GPS Longitude : " + locationGps!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            textView_resultado.append("\nNetwork ")
                            textView_resultado.append("\nLatitude : " + locationNetwork!!.latitude)
                            textView_resultado.append("\nLongitude : " + locationNetwork!!.longitude)
                            Log.d(LOCATION_STATUS, " Network Latitude : " + locationNetwork!!.latitude)
                            Log.d(LOCATION_STATUS, " Network Longitude : " + locationNetwork!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    textView_resultado.append("\nNetwork ")
                    textView_resultado.append("\nLatitude : " + locationNetwork!!.latitude)
                    textView_resultado.append("\nLongitude : " + locationNetwork!!.longitude)
                    Log.d(LOCATION_STATUS, " Network Latitude : " + locationNetwork!!.latitude)
                    Log.d(LOCATION_STATUS, " Network Longitude : " + locationNetwork!!.longitude)
                }else{
                    textView_resultado.append("\nGPS ")
                    textView_resultado.append("\nLatitude : " + locationGps!!.latitude)
                    textView_resultado.append("\nLongitude : " + locationGps!!.longitude)
                    Log.d(LOCATION_STATUS, " GPS Latitude : " + locationGps!!.latitude)
                    Log.d(LOCATION_STATUS, " GPS Longitude : " + locationGps!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }


    // Pedir permisos de Ubicacion
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }


    // Resultado de pedir los permisos de ubicacion
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()

        }
    }


    // Codigo del Servicio


    fun startService(v: View?, input: String) {
        val input = input
        val serviceIntent = Intent(this, ExampleService::class.java)
        serviceIntent.putExtra("inputExtra", input)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService(v: View?) {
        val serviceIntent = Intent(this, ExampleService::class.java)
        stopService(serviceIntent)
    }
}
