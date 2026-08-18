# Microsoft Store API を直接叩いてテストフライトにMSIXパッケージをアップロードするスクリプト
$ErrorActionPreference = "Stop"

$tenantId = $env:MS_STORE_TENANT_ID
$clientId = $env:MS_STORE_CLIENT_ID
$clientSecret = $env:MS_STORE_CLIENT_SECRET
$flightId = $env:MS_STORE_FLIGHT_ID
$appId = "9N3XXRFZ4G1R"

if (-not $tenantId -or -not $clientId -or -not $clientSecret -or -not $flightId) {
    Write-Error "Required environment variables (MS_STORE_TENANT_ID, MS_STORE_CLIENT_ID, MS_STORE_CLIENT_SECRET, MS_STORE_FLIGHT_ID) are not set."
}

# 1. Azure AD からアクセストークンを取得
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

# 2. フライト情報および申請（Submission）の取得または作成
Write-Host "Retrieving or creating a flight submission..."
$submissionUri = "https://manage.devcenter.microsoft.com/v1.0/my/applications/$appId/flights/$flightId/submissions"

try {
    # 新規の申請を作成（すでに進行中の申請がない場合のみ成功）
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
        # 進行中の申請がすでに存在する場合は、最後の申請情報を取得する
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

# 3. アップロード用パッケージ（ZIP）の作成
# ConveyorでビルドされたMSIXファイルを探す
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

# Microsoft Submission APIの仕様上、パッケージはZIPアーカイブ（packages.zip）に固めてアップロードする必要があります
$zipPath = Join-Path $pwd "packages.zip"
if (Test-Path $zipPath) { Remove-Item $zipPath }
Write-Host "Archiving MSIX to packages.zip..."
Compress-Archive -Path $msixFile.FullName -DestinationPath $zipPath

# 4. Azure Blob Storage (SAS URL) へのZIPファイルのアップロード
Write-Host "Uploading packages.zip to Azure Blob Storage..."
$uploadHeaders = @{
    "x-ms-blob-type" = "BlockBlob"
}
Invoke-RestMethod -Uri $fileUploadUrl -Method Put -Headers $uploadHeaders -InFile $zipPath
Write-Host "Upload completed successfully."

# アップロード後に一時ZIPファイルをクリーンアップ
Remove-Item $zipPath

# 5. 申請情報の更新 (既存パッケージを PendingDelete マークし、新規パッケージを追加)
Write-Host "Updating flight submission packages data..."
$updatedPackages = @()
# 既存のパッケージがすでに登録されている場合、すべて PendingDelete に設定して維持する
if ($submission.flightPackages) {
    foreach ($pkg in $submission.flightPackages) {
        # 既存のオブジェクトからプロパティを安全にハッシュテーブルへコピー
        $copiedPkg = @{}
        foreach ($prop in $pkg.PSObject.Properties) {
            $copiedPkg[$prop.Name] = $prop.Value
        }
        # state プロパティを追加・上書き
        $copiedPkg["state"] = "PendingDelete"
        $updatedPackages += $copiedPkg
    }
}
# 新しくアップロードしたパッケージを PendingUpload として追加
$newPackage = @{
    fileName = $msixFilename
    fileRequestName = $msixFilename
    state = "PendingUpload"
}
$updatedPackages += $newPackage
# 申請データのパッケージ情報を差し替え
$submission.flightPackages = $updatedPackages

# PUTリクエスト用のボディデータを生成 (更新された申請情報JSON)
$submissionJson = $submission | ConvertTo-Json -Depth 10 -Compress
$updateUri = "$submissionUri/$submissionId"
$updateResponse = Invoke-RestMethod -Uri $updateUri -Method Put -Headers $headers -Body $submissionJson
Write-Host "Flight submission packages updated successfully."

# 6. 申請の送信（コミット）
Write-Host "Committing the flight submission..."
$commitUri = "$updateUri/commit"
$commitResponse = Invoke-RestMethod -Uri $commitUri -Method Post -Headers $headers
Write-Host "Flight submission committed successfully! Microsoft Store is now processing the package."
