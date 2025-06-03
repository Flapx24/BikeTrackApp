package com.example.biketrack.presentation.components

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.biketrack.domain.entities.GeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint as OSMGeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun RouteMapView(
    calculatedRoutePoints: List<GeoPoint>?,
    routePoints: List<GeoPoint>?,
    routeTitle: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    
    // Determine which points to use
    val pointsToDisplay = calculatedRoutePoints ?: routePoints
    
    if (pointsToDisplay.isNullOrEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos de ruta para mostrar en el mapa",
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
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)
                    zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                    isHorizontalMapRepetitionEnabled = false
                    isVerticalMapRepetitionEnabled = false
                    
                    // Calculate bounds for the route
                    val latitudes = pointsToDisplay.map { it.lat }
                    val longitudes = pointsToDisplay.map { it.lng }
                    
                    // Create bounding box
                    val minLat = latitudes.minOrNull() ?: 0.0
                    val maxLat = latitudes.maxOrNull() ?: 0.0
                    val minLng = longitudes.minOrNull() ?: 0.0
                    val maxLng = longitudes.maxOrNull() ?: 0.0
                    
                    // Add padding to the bounds (5% on each side)
                    val latPadding = (maxLat - minLat) * 0.05
                    val lngPadding = (maxLng - minLng) * 0.05
                    
                    val boundingBox = org.osmdroid.util.BoundingBox(
                        maxLat + latPadding, // North
                        maxLng + lngPadding, // East  
                        minLat - latPadding, // South
                        minLng - lngPadding  // West
                    )
                    
                    // Set the view to fit the bounding box
                    post {
                        zoomToBoundingBox(boundingBox, false, 50)
                    }
                    
                    // Add route visualization
                    if (calculatedRoutePoints != null) {
                        // Show calculated route as a continuous line
                        addCalculatedRoute(this, calculatedRoutePoints)
                    } else {
                        // Show original points with numbers and connecting lines
                        addOriginalRoutePoints(this, pointsToDisplay)
                    }
                }
            },
            update = { mapView ->
                // Ensure proper gesture handling
                mapView.setMultiTouchControls(true)
                mapView.setBuiltInZoomControls(false)
                mapView.isHorizontalMapRepetitionEnabled = false
                mapView.isVerticalMapRepetitionEnabled = false
                
                // Clear existing overlays
                mapView.overlays.clear()
                
                // Re-add route visualization
                if (calculatedRoutePoints != null) {
                    addCalculatedRoute(mapView, calculatedRoutePoints)
                } else if (routePoints != null) {
                    addOriginalRoutePoints(mapView, routePoints)
                }
                
                // Recalculate bounds if needed
                val pointsToUse = calculatedRoutePoints ?: routePoints
                if (!pointsToUse.isNullOrEmpty()) {
                    val latitudes = pointsToUse.map { it.lat }
                    val longitudes = pointsToUse.map { it.lng }
                    
                    val minLat = latitudes.minOrNull() ?: 0.0
                    val maxLat = latitudes.maxOrNull() ?: 0.0
                    val minLng = longitudes.minOrNull() ?: 0.0
                    val maxLng = longitudes.maxOrNull() ?: 0.0
                    
                    val latPadding = (maxLat - minLat) * 0.05
                    val lngPadding = (maxLng - minLng) * 0.05
                    
                    val boundingBox = org.osmdroid.util.BoundingBox(
                        maxLat + latPadding,
                        maxLng + lngPadding, 
                        minLat - latPadding,
                        minLng - lngPadding
                    )
                    
                    mapView.post {
                        mapView.zoomToBoundingBox(boundingBox, false, 50)
                    }
                }
                
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun addCalculatedRoute(mapView: MapView, points: List<GeoPoint>) {
    val polyline = Polyline().apply {
        // Convert points to OSM format
        setPoints(points.map { OSMGeoPoint(it.lat, it.lng) })
        
        // Style the line
        outlinePaint.color = AndroidColor.parseColor("#4CAF50") // Primary green
        outlinePaint.strokeWidth = 8f
        outlinePaint.isAntiAlias = true
    }
    
    mapView.overlays.add(polyline)
}

private fun addOriginalRoutePoints(mapView: MapView, points: List<GeoPoint>) {
    // Add numbered markers for each point
    points.forEachIndexed { index, point ->
        val marker = Marker(mapView).apply {
            position = OSMGeoPoint(point.lat, point.lng)
            title = "Punto ${index + 1}"
            
            // Create custom icon with number
            icon = createNumberedMarkerIcon(mapView.context, index + 1)
        }
        mapView.overlays.add(marker)
    }
    
    // Add connecting lines between points
    if (points.size > 1) {
        val polyline = Polyline().apply {
            setPoints(points.map { OSMGeoPoint(it.lat, it.lng) })
            
            // Style the connecting line
            outlinePaint.color = AndroidColor.parseColor("#2196F3") // Blue for original route
            outlinePaint.strokeWidth = 4f
            outlinePaint.isAntiAlias = true
            outlinePaint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 5f), 0f)
        }
        mapView.overlays.add(polyline)
    }
}

private fun createNumberedMarkerIcon(context: Context, number: Int): android.graphics.drawable.Drawable {
    // Create a simple colored circle with number
    val size = (32 * context.resources.displayMetrics.density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    
    // Draw circle
    val paint = android.graphics.Paint().apply {
        color = AndroidColor.parseColor("#4CAF50")
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    
    // Draw number
    val textPaint = android.graphics.Paint().apply {
        color = AndroidColor.WHITE
        textSize = 16 * context.resources.displayMetrics.density
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    
    val textBounds = android.graphics.Rect()
    textPaint.getTextBounds(number.toString(), 0, number.toString().length, textBounds)
    val textY = size / 2f + textBounds.height() / 2f
    
    canvas.drawText(number.toString(), size / 2f, textY, textPaint)
    
    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
} 