# --------------------------------------------------------------
# install‑dfbrs‑prereqs.ps1
# --------------------------------------------------------------
# Run this script **as Administrator**.
# --------------------------------------------------------------

# 1️⃣ Helper to download a file
function Download-File($url, $dest) {
    Write-Host "Downloading $url ..."
    Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing
}

# 2️⃣ Working folder (temp)
$work = "$env:TEMP\dfbrs-prereqs"
New-Item -ItemType Directory -Force -Path $work | Out-Null

# -----------------------------------------------------------------
# 3️⃣ Java 21 (Adoptium) – silent MSI install
# -----------------------------------------------------------------
$javaUrl = "https://github.com/adoptium/temurin21-binaries/releases/latest/download/OpenJDK21U-jdk_x64_windows_hotspot.msi"
$javaMsi = "$work\jdk21.msi"
Download-File $javaUrl $javaMsi

Write-Host "Installing Java 21..."
Start-Process msiexec.exe -ArgumentList "/i `"$javaMsi`" /quiet INSTALLDIR=`"$env:ProgramFiles\Java\jdk-21`"" -Wait -NoNewWindow

# Set JAVA_HOME & PATH
$javaHome = "$env:ProgramFiles\Java\jdk-21"
[Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, [EnvironmentVariableTarget]::Machine)
$path = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::Machine)
if (-not ($path -like "*$javaHome\bin*")) {
    [Environment]::SetEnvironmentVariable("Path", "$path;$javaHome\bin", [EnvironmentVariableTarget]::Machine)
}

# -----------------------------------------------------------------
# 4️⃣ Maven (optional) – unzip manual install
# -----------------------------------------------------------------
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
$mavenZip = "$work\maven.zip"
Download-File $mavenUrl $mavenZip

$installDir = "C:\Program Files\Apache\Maven"
Write-Host "Extracting Maven to $installDir ..."
Expand-Archive -Path $mavenZip -DestinationPath $installDir -Force
# The archive contains a top‑level folder; move its contents up
Move-Item -Path "$installDir\apache-maven-3.9.9\*" -Destination $installDir -Force
Remove-Item "$installDir\apache-maven-3.9.9" -Recurse -Force

$env:MAVEN_HOME = $installDir
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $installDir, [EnvironmentVariableTarget]::Machine)
$path = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::Machine)
if (-not ($path -like "*$installDir\bin*")) {
    [Environment]::SetEnvironmentVariable("Path", "$path;$installDir\bin", [EnvironmentVariableTarget]::Machine)
}

# -----------------------------------------------------------------
# 5️⃣ Node 20 (MSI) – silent install
# -----------------------------------------------------------------
$nodeUrl = "https://nodejs.org/dist/v20.14.0/node-v20.14.0-x64.msi"
$nodeMsi = "$work\node.msi"
Download-File $nodeUrl $nodeMsi

Write-Host "Installing Node.js..."
Start-Process msiexec.exe -ArgumentList "/i `"$nodeMsi`" /quiet" -Wait -NoNewWindow

# -----------------------------------------------------------------
# 6️⃣ Docker Desktop – GUI installer (cannot be fully silent)
# -----------------------------------------------------------------
$dockerUrl = "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"
$dockerExe = "$work\DockerDesktopInstaller.exe"
Download-File $dockerUrl $dockerExe

Write-Host "Launching Docker Desktop installer..."
Start-Process -FilePath $dockerExe -Wait

# -----------------------------------------------------------------
# 7️⃣ Git (optional) – silent install
# -----------------------------------------------------------------
$gitUrl = "https://github.com/git-for-windows/git/releases/latest/download/Git-2.45.2-64-bit.exe"
$gitExe = "$work\git-installer.exe"
Download-File $gitUrl $gitExe

Write-Host "Installing Git..."
Start-Process -FilePath $gitExe -ArgumentList "/VERYSILENT" -Wait

# -----------------------------------------------------------------
# 8️⃣ Verify installations
# -----------------------------------------------------------------
Write-Host "`n=== Verification ==="
& java -version
& mvn -version
& node -v
& npm -v
& git --version
Write-Host "Docker Desktop should now be visible in the Start menu. Open it and ensure it finishes its first‑run setup."

Write-Host "`nAll done! You may need to **restart your terminal** (or log out / log back in) for PATH changes to take effect."