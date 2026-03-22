# Arrêter tous les daemons Gradle pour éviter les conflits
Write-Host "Stopping any running Gradle daemons..."
.\gradlew --stop

# Compiler le projet sans daemon
Write-Host "Compiling the project..."
.\gradlew assembleDebug --no-daemon

# Installer l'APK sur ton téléphone
Write-Host "Installing APK on device..."
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Lancer l'application
Write-Host "Launching the app..."
adb shell am start -n com.example.insatlkotlinv1/.MainActivity