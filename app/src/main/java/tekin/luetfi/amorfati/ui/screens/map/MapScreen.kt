package tekin.luetfi.amorfati.ui.screens.map

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import tekin.luetfi.amorfati.domain.model.TarotCard
import tekin.luetfi.amorfati.utils.Deck

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    onMarkerClick: (TarotCard) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    // Remember MapView
    val mapView = remember {
        MapView(context).apply { onCreate(null) }
    }

    // Lifecycle handling
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize().padding(8.dp)
    ) { mv ->
        mv.getMapAsync { map ->
            map.setStyle("https://demotiles.maplibre.org/style.json") { style ->
                // Symbol manager for markers
                val symbolManager = SymbolManager(mv, map, style).apply {
                    iconAllowOverlap = true
                    iconIgnorePlacement = true
                }
                // Load coordinates.json from assets with Moshi
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                val coordsType = Types.newParameterizedType(
                    Map::class.java,
                    String::class.java,
                    Coordinate::class.java
                )
                val coordsAdapter = moshi.adapter<Map<String, Coordinate>>(coordsType)
                val coordsJson = context.assets.open("coordinates.json").bufferedReader().use { it.readText() }
                val coordsMap: Map<String, Coordinate> = coordsAdapter.fromJson(coordsJson) ?: emptyMap()


                // Define Germany bounds (roughly)
                val germanyBounds = LatLngBounds.from(55.1, 15.0, 47.2, 5.9)
                val idToCard = mutableMapOf<Long, TarotCard>()
                Deck.fullDeck.forEach { card ->
                    val coord = coordsMap[card.code] ?: return@forEach
                    val key = card.code.lowercase()
                    try {
                        val bmp = context.assets.open("lowres/$key.png").use {
                            BitmapFactory.decodeStream(it)
                        }
                        style.addImage(key, bmp)
                    } catch (_: Exception) {
                        val bmp = context.assets.open("lowres/back.png").use {
                            BitmapFactory.decodeStream(it)
                        }
                        style.addImage(key, bmp)
                    }
                    val symbolOptions = SymbolOptions()
                        .withLatLng(LatLng(coord.lat, coord.lng))
                        .withIconImage(key)
                        .withIconSize(1.0f)
                    val symbol: Symbol = symbolManager.create(symbolOptions)
                    idToCard[symbol.id] = card
                }

                // Constrain camera and zoom to Germany
                map.setLatLngBoundsForCameraTarget(germanyBounds)
                //map.setMinZoom(5.0)
                //map.setMaxZoom(7.0)

                // handle marker taps only once
                symbolManager.addClickListener { symbol ->
                    idToCard[symbol.id]?.let { onMarkerClick(it) }
                    true
                }
            }
        }
    }
}

// Helper data class for JSON parsing
private data class Coordinate(val lat: Double, val lng: Double)
