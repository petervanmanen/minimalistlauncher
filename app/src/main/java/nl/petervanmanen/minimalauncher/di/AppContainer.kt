package nl.petervanmanen.minimalauncher.di

import android.content.Context
import nl.petervanmanen.minimalauncher.data.repository.AppRepository
import nl.petervanmanen.minimalauncher.data.repository.DockRepository
import nl.petervanmanen.minimalauncher.data.repository.MapRepository
import nl.petervanmanen.minimalauncher.data.repository.NotificationRepository
import nl.petervanmanen.minimalauncher.data.repository.WeatherRepository
import nl.petervanmanen.minimalauncher.location.LocationProvider

/** Minimal manual service locator — no DI framework needed at this app's scale. */
class AppContainer(context: Context) {
    val appRepository = AppRepository(context)
    val dockRepository = DockRepository(context)
    val locationProvider = LocationProvider(context)
    val weatherRepository = WeatherRepository(locationProvider)
    val mapRepository = MapRepository()
    val notificationRepository = NotificationRepository(context)
}
