package xing.dev.alarm_app.di

import br.ufc.insight.ai4wellness.di.serviceModule
import org.koin.core.context.GlobalContext.loadKoinModules

fun injectMobileFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            databaseModule, repositoryModule, viewModelModule, serviceModule, workerModule
        )
    )
}