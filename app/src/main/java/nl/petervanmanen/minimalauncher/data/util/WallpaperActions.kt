package nl.petervanmanen.minimalauncher.data.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sets a pure black wallpaper on both the home and lock screen — true black
 * on an OLED display, matching the launcher's own background, without
 * needing any manual Settings navigation.
 */
suspend fun setBlackWallpaper(context: Context): Boolean = withContext(Dispatchers.IO) {
    runCatching {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.BLACK) }
        WallpaperManager.getInstance(context)
            .setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
        bitmap.recycle()
    }.isSuccess
}
