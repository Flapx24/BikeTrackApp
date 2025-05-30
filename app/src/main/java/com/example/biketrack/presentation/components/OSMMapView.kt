package com.example.biketrack.presentation.components

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.biketrack.domain.entities.Workshop
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMMapView(
    workshops: List<Workshop>,
    selectedWorkshop: Workshop? = null,
    onWorkshopClick: (Workshop) -> Unit,
    onCloseMap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    
    if (workshops.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay talleres para mostrar en el mapa",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = "BikeTrack/1.0"
            osmdroidBasePath = context.filesDir
            osmdroidTileCache = context.cacheDir.resolve("osmdroid")
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                    
                    // Calculate bounds and center
                    val latitudes = workshops.map { it.coordinates.lat }
                    val longitudes = workshops.map { it.coordinates.lng }
                    
                    val centerLat = latitudes.average()
                    val centerLng = longitudes.average()
                    
                    // Set initial view
                    controller.setCenter(GeoPoint(centerLat, centerLng))
                    controller.setZoom(12.0)
                    
                    // Add markers for workshops
                    workshops.forEach { workshop ->
                        val marker = Marker(this).apply {
                            position = GeoPoint(workshop.coordinates.lat, workshop.coordinates.lng)
                            title = workshop.name
                            snippet = workshop.address
                            
                            // Custom icon
                            icon = createMarkerIcon(
                                ctx, 
                                isSelected = workshop.id == selectedWorkshop?.id
                            )
                            
                            // Click listener
                            setOnMarkerClickListener { _, _ ->
                                onWorkshopClick(workshop)
                                true
                            }
                        }
                        overlays.add(marker)
                    }
                    
                    // If there's a selected workshop, center on it
                    selectedWorkshop?.let { workshop ->
                        controller.animateTo(
                            GeoPoint(workshop.coordinates.lat, workshop.coordinates.lng)
                        )
                        controller.setZoom(15.0)
                    }
                }
            },
            update = { mapView ->
                // Update markers when selected workshop changes
                mapView.overlays.clear()
                
                workshops.forEach { workshop ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(workshop.coordinates.lat, workshop.coordinates.lng)
                        title = workshop.name
                        snippet = workshop.address
                        
                        icon = createMarkerIcon(
                            context, 
                            isSelected = workshop.id == selectedWorkshop?.id
                        )
                        
                        setOnMarkerClickListener { _, _ ->
                            onWorkshopClick(workshop)
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
                
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Map info overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${workshops.size} taller${if (workshops.size != 1) "es" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Close button (X) - top right
        onCloseMap?.let { closeAction ->
            IconButton(
                onClick = closeAction,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(50)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar mapa",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Zoom controls - bottom right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
        ) {
            // Zoom in (+)
            IconButton(
                onClick = { 
                    mapView?.controller?.zoomIn()
                },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Acercar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Zoom out (-)
            IconButton(
                onClick = { 
                    mapView?.controller?.zoomOut()
                },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Alejar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun createMarkerIcon(context: Context, isSelected: Boolean): BitmapDrawable {
    // Create a drawable from Material Icon
    val drawable = ContextCompat.getDrawable(
        context, 
        android.R.drawable.ic_menu_mylocation
    )?.mutate()
    
    if (drawable != null) {
        // Tint the icon based on selection state
        val color = if (isSelected) {
            Color(0xFF4CAF50).toArgb() // Primary green
        } else {
            Color(0xFFD32F2F).toArgb() // Error red
        }
        
        DrawableCompat.setTint(drawable, color)
        
        // Scale the drawable
        val size = (48 * context.resources.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, size, size)
        
        return drawable as BitmapDrawable
    }
    
    // Fallback - should never happen
    return BitmapDrawable(context.resources, null as android.graphics.Bitmap?)
} 