pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HolaMundo"

// Mismo esquema que AC Móvil: módulos hoja (core, desing) y un módulo shell (app).
include(":core")    // dominio, datos, casos de uso, DI base (como :core de AC Móvil)
include(":desing")  // sistema de diseño compartido (se escribe "desing" igual que en AC Móvil)
include(":app")     // shell: Application, injector, Activity
