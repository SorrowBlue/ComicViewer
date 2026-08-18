# Upload MSIX package to Microsoft Store Flight using Submission API
$ErrorActionPreference = "Stop"

$tenantId = $env:MS_STORE_TENANT_ID
$clientId = $env:MS_STORE_CLIENT_ID
$clientSecret = $env:MS_STORE_CLIENT_SECRET
$flightId = $env:MS_STORE_FLIGHT_ID
$appId = "9N3XXRFZ4G1R"

if (-not $tenantId -or -not $clientId -or -not $clientSecret -or -not $flightId) {
    Write-Error "Required environment variables (MS_STORE_TENANT_ID, MS_STORE_CLIENT_ID, MS_STORE_CLIENT_SECRET, MS_STORE_FLIGHT_ID) are not set."
}

# 1. Get Access Token from Azure AD
Write-Host "Authenticating with Microsoft Partner Center API..."
$body = @{
    grant_type    = "client_credentials"
    client_id     = $clientId
    client_secret = $clientSecret
    resource      = "https://manage.devcenter.microsoft.com"
}
$authResponse = Invoke-RestMethod -Uri "https://login.microsoftonline.com/$tenantId/oauth2/token" -Method Post -Body $body
$token = $authResponse.access_token
$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. Get or Create Flight Submission
Write-Host "Retrieving or creating a flight submission..."
$submissionUri = "https://manage.devcenter.microsoft.com/v1.0/my/applications/$appId/flights/$flightId/submissions"

try {
    # Try to create a new submission
    $submission = Invoke-RestMethod -Uri $submissionUri -Method Post -Headers $headers
    Write-Host "Created a new flight submission (ID: $($submission.id))"
} catch {
    $errMsg = $_.Exception.Message
    if ($_.Exception.Response) {
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $errResponse = $reader.ReadToEnd()
            $errMsg += " | Response: $errResponse"
        } catch {}
    }
    Write-Host "Could not create a new submission, trying to retrieve last active submission. Detail: $errMsg"
    try {
        # Get the last active submission if creation failed
        $submission = Invoke-RestMethod -Uri "$submissionUri/last" -Method Get -Headers $headers
        Write-Host "Retrieved existing flight submission (ID: $($submission.id))"
    } catch {
        $lastErrMsg = $_.Exception.Message
        if ($_.Exception.Response) {
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $lastErrResponse = $reader.ReadToEnd()
                $lastErrMsg += " | Response: $lastErrResponse"
            } catch {}
        }
        Write-Error "Failed to retrieve the last submission. Detail: $lastErrMsg"
    }
}

$submissionId = $submission.id
$fileUploadUrl = $submission.fileUploadUrl

if (-not $submissionId -or -not $fileUploadUrl) {
    Write-Warning "Submission ID or File Upload URL is empty. Raw API Response:"
    $submission | Out-String | Write-Host
}

# 3. Create Package ZIP
# Find MSIX package built by Conveyor
$outputDir = "$pwd\output"
if (-not (Test-Path $outputDir)) {
    $outputDir = "../output"
}
if (-not (Test-Path $outputDir)) {
    Write-Error "Output directory path does not exist: $outputDir"
}
$msixFiles = Get-ChildItem -Path $outputDir -Filter "*.msix"
if ($msixFiles.Count -eq 0) {
    Write-Error "No MSIX package found in output directory ($outputDir)."
}
$msixFile = $msixFiles[0]
$msixFilename = $msixFile.Name
Write-Host "Found MSIX package: $msixFilename"

# Archive MSIX to packages.zip as required by MS Store API
$zipPath = Join-Path $pwd "packages.zip"
if (Test-Path $zipPath) { Remove-Item $zipPath }
Write-Host "Archiving MSIX to packages.zip..."

# Use .NET ZipFile to ensure cross-platform compatibility and avoid ZIP header corruption on Linux
Add-Type -AssemblyName System.IO.Compression.FileSystem
$tempDir = Join-Path $pwd "temp_zip_dir"
if (Test-Path $tempDir) { Remove-Item $tempDir -Recurse -Force }
$null = New-Item -ItemType Directory -Path $tempDir
Copy-Item -Path $msixFile.FullName -DestinationPath $tempDir
[System.IO.Compression.ZipFile]::CreateFromDirectory($tempDir, $zipPath)
Remove-Item $tempDir -Recurse -Force

# 4. Upload ZIP to Azure Blob Storage
Write-Host "Uploading packages.zip to Azure Blob Storage..."
$uploadHeaders = @{
    "x-ms-blob-type" = "BlockBlob"
}
Invoke-RestMethod -Uri $fileUploadUrl -Method Put -Headers $uploadHeaders -InFile $zipPath
Write-Host "Upload completed successfully."

Remove-Item $zipPath

# 5. Update Submission Package List (Set existing packages to PendingDelete)
Write-Host "Updating flight submission packages data..."

$updatedPackages = @()

if ($submission.flightPackages) {
    foreach ($pkg in $submission.flightPackages) {
        $copiedPkg = New-Object System.Collections.Hashtable
        foreach ($prop in $pkg.PSObject.Properties) {
            $copiedPkg[$prop.Name] = $prop.Value
        }
        $copiedPkg["state"] = "PendingDelete"
        $updatedPackages += $copiedPkg
    }
}

# Add new package as PendingUpload
$newPackage = New-Object System.Collections.Hashtable
$newPackage["fileName"] = $msixFilename
$newPackage["fileRequestName"] = $msixFilename
$newPackage["state"] = "PendingUpload"
$updatedPackages += $newPackage

$submission.flightPackages = $updatedPackages

# Generate updated JSON and PUT to API
$submissionJson = $submission | ConvertTo-Json -Depth 10 -Compress
$updateUri = "$submissionUri/$submissionId"
$updateResponse = Invoke-RestMethod -Uri $updateUri -Method Put -Headers $headers -Body $submissionJson
Write-Host "Flight submission packages updated successfully."

# 6. Commit Submission
Write-Host "Committing the flight submission..."
$commitUri = "$updateUri/commit"
$commitResponse = Invoke-RestMethod -Uri $commitUri -Method Post -Headers $headers
Write-Host "Flight submission committed successfully! Microsoft Store is now processing the package."
