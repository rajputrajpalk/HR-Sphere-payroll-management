# PowerShell Starter Script for HR Sphere

$jdk = Get-ChildItem -Path "C:\Program Files\Eclipse Adoptium", "C:\Program Files\Java", "C:\Program Files\Amazon Corretto" -Filter "jdk*" -ErrorAction SilentlyContinue | Select-Object -First 1

if ($jdk) {
    $env:JAVA_HOME = $jdk.FullName
    Write-Host "Auto-detected JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
    mvn spring-boot:run
} else {
    Write-Host "JDK not found in standard paths. Please set `$env:JAVA_HOME." -ForegroundColor Red
}
