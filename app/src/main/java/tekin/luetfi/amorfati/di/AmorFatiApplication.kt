package tekin.luetfi.amorfati.di

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

@HiltAndroidApp
class AmorFatiApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MapLibre.getInstance(this, "", WellKnownTileServer.MapLibre)
    }


}