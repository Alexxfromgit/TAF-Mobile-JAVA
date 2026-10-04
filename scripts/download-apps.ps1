# Downloads the Sauce Labs "My Demo App" builds used by the examples into examples/apps/.
# The apps are NOT redistributed by this repository: they are fetched from the official Sauce Labs releases.
param([ValidateSet("android", "ios", "all")][string]$Platform = "all")
$ErrorActionPreference = "Stop"

$version = if ($env:MDA_VERSION) { $env:MDA_VERSION } else { "2.3.0" }
$androidBuild = if ($env:MDA_ANDROID_BUILD) { $env:MDA_ANDROID_BUILD } else { "27" }
$target = Join-Path (Split-Path -Parent $PSScriptRoot) "examples/apps"
New-Item -ItemType Directory -Force $target | Out-Null

function Get-App([string]$Url, [string]$File) {
    $path = Join-Path $target $File
    if (Test-Path $path) { Write-Host "already present: $File"; return }
    Write-Host "downloading $File"
    Invoke-WebRequest -Uri $Url -OutFile "$path.part"
    Move-Item "$path.part" $path
}

if ($Platform -in "android", "all") {
    Get-App "https://github.com/saucelabs/my-demo-app-android/releases/download/$version/mda-$version-$androidBuild.apk" "mda-$version-$androidBuild.apk"
}
if ($Platform -in "ios", "all") {
    Get-App "https://github.com/saucelabs/my-demo-app-ios/releases/download/$version/SauceLabs-Demo-App.Simulator.zip" "SauceLabs-Demo-App.Simulator.zip"
}
Write-Host "apps in $target"
