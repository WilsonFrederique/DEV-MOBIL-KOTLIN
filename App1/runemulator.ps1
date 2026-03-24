# Configuration
$androidSdk = "C:\Users\FREDERIQUE\AppData\Local\Android\Sdk"
$avdName = "Pixel_3"

# Ajouter au PATH si nécessaire
$env:Path += ";$androidSdk\platform-tools"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Deploiement de l'application" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Vérifier si un appareil est connecté
Write-Host "`n1. Verification des appareils..." -ForegroundColor Yellow
$devices = adb devices

if ($devices -notmatch "device$") {
    Write-Host "   Aucun appareil detecte!" -ForegroundColor Red
    Write-Host "   Demarrage de l'emulateur: $avdName" -ForegroundColor Yellow
    
    # Ajouter le chemin de l'émulateur
    $env:Path += ";$androidSdk\emulator"
    
    # Démarrer l'émulateur
    Start-Process -NoNewWindow -FilePath "$androidSdk\emulator\emulator.exe" -ArgumentList "-avd", $avdName
    
    Write-Host "   Attente du demarrage (60 secondes)..." -ForegroundColor Yellow
    Start-Sleep -Seconds 60
    
    # Réinitialiser ADB si nécessaire
    Write-Host "   Reinitialisation ADB..." -ForegroundColor Yellow
    adb kill-server
    Start-Sleep -Seconds 2
    adb start-server
    Start-Sleep -Seconds 5
    
    # Vérifier à nouveau
    $devices = adb devices
    if ($devices -notmatch "device$") {
        Write-Host "   ❌ L'emulateur ne repond pas!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "   ✓ Appareil detecte!" -ForegroundColor Green
adb devices

# Arrêter les daemons Gradle
Write-Host "`n2. Arret des daemons Gradle..." -ForegroundColor Yellow
.\gradlew --stop

# Compiler le projet
Write-Host "`n3. Compilation du projet..." -ForegroundColor Yellow
.\gradlew assembleDebug --no-daemon

if ($LASTEXITCODE -ne 0) {
    Write-Host "   ❌ Erreur de compilation!" -ForegroundColor Red
    exit 1
}
Write-Host "   ✓ Compilation reussie!" -ForegroundColor Green

# Installer l'APK
Write-Host "`n4. Installation de l'APK..." -ForegroundColor Yellow
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"

if (Test-Path $apkPath) {
    adb install -r $apkPath
    Write-Host "   ✓ APK installe avec succes!" -ForegroundColor Green
} else {
    Write-Host "   ❌ APK non trouve!" -ForegroundColor Red
    exit 1
}

# Lancer l'application
Write-Host "`n5. Lancement de l'application..." -ForegroundColor Yellow
adb shell am start -n com.example.insatlkotlinv1/.MainActivity

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "   ✅ Application lancee avec succes!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green