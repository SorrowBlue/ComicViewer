# Upload MSIX package to Microsoft Store Flight using Submission API
import os
import sys
import json
import urllib.request
import urllib.parse
import urllib.error
import zipfile
import glob

def print_http_error(e, action_name):
    """Helper to print detailed HTTP error response from Microsoft Ingestion API"""
    print(f"\n[Error] {action_name} failed with HTTP Status: {e.code} {e.reason}", file=sys.stderr)
    try:
        body = e.read().decode("utf-8")
        try:
            # Try to format JSON error responses for readability
            parsed_json = json.loads(body)
            print("Response details (JSON):", file=sys.stderr)
            print(json.dumps(parsed_json, indent=2), file=sys.stderr)
        except json.JSONDecodeError:
            print(f"Response details (Raw): {body}", file=sys.stderr)
    except Exception as read_err:
        print(f"Could not read HTTP error response body: {read_err}", file=sys.stderr)

try:
    # 1. Get Environment Variables
    tenant_id = os.environ.get("MS_STORE_TENANT_ID")
    client_id = os.environ.get("MS_STORE_CLIENT_ID")
    client_secret = os.environ.get("MS_STORE_CLIENT_SECRET")
    flight_id = os.environ.get("MS_STORE_FLIGHT_ID")
    app_id = "9N3XXRFZ4G1R"

    if not all([tenant_id, client_id, client_secret, flight_id]):
        print("Error: Required environment variables (MS_STORE_TENANT_ID, MS_STORE_CLIENT_ID, MS_STORE_CLIENT_SECRET, MS_STORE_FLIGHT_ID) are not set.", file=sys.stderr)
        sys.exit(1)

    # 2. Get Access Token from Azure AD
    print("Authenticating with Microsoft Partner Center API...")
    token_url = f"https://login.microsoftonline.com/{tenant_id}/oauth2/token"
    token_data = urllib.parse.urlencode({
        "grant_type": "client_credentials",
        "client_id": client_id,
        "client_secret": client_secret,
        "resource": "https://manage.devcenter.microsoft.com"
    }).encode("utf-8")

    req = urllib.request.Request(token_url, data=token_data, method="POST")
    try:
        with urllib.request.urlopen(req) as response:
            auth_res = json.loads(response.read().decode("utf-8"))
            token = auth_res["access_token"]
    except urllib.error.HTTPError as e:
        print_http_error(e, "Authentication")
        sys.exit(1)

    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # 3. Get or Create Flight Submission
    print("Retrieving or creating a flight submission...")
    submission_url = f"https://manage.devcenter.microsoft.com/v1.0/my/applications/{app_id}/flights/{flight_id}/submissions"

    # Try to create a new submission (POST)
    req = urllib.request.Request(submission_url, headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req) as response:
            submission = json.loads(response.read().decode("utf-8"))
            print(f"Created a new flight submission (ID: {submission['id']})")
    except urllib.error.HTTPError as e:
        # If active submission already exists, get the last one (GET)
        print("An active submission already exists or POST failed. Trying to retrieve last submission...")
        last_url = f"{submission_url}/last"
        req_last = urllib.request.Request(last_url, headers=headers, method="GET")
        try:
            with urllib.request.urlopen(req_last) as response:
                submission = json.loads(response.read().decode("utf-8"))
                print(f"Retrieved existing flight submission (ID: {submission['id']})")
        except urllib.error.HTTPError as e_last:
            print_http_error(e_last, "Retrieve last submission")
            sys.exit(1)

    submission_id = submission["id"]
    file_upload_url = submission["fileUploadUrl"]

    if not submission_id or not file_upload_url:
        print("Warning: Submission ID or File Upload URL is empty. Raw API Response:", file=sys.stderr)
        print(json.dumps(submission, indent=2), file=sys.stderr)

    # 4. Locate MSIX Package
    print("Locating MSIX package...")
    msix_pattern = os.path.join("output", "*.msix")
    msix_files = glob.glob(msix_pattern)
    if not msix_files:
        # Fallback to parent output directory
        msix_files = glob.glob(os.path.join("..", "output", "*.msix"))

    if not msix_files:
        print("Error: No MSIX package found in output directory.", file=sys.stderr)
        sys.exit(1)

    msix_file_path = msix_files[0]
    msix_filename = os.path.basename(msix_file_path)
    print(f"Found MSIX package: {msix_filename}")

    # Archive MSIX to packages.zip
    zip_path = "packages.zip"
    if os.path.exists(zip_path):
        os.remove(zip_path)

    print("Archiving MSIX to packages.zip...")
    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zip_file:
        zip_file.write(msix_file_path, msix_filename)

    # 5. Upload ZIP to Azure Blob Storage (PUT)
    print("Uploading packages.zip to Azure Blob Storage...")
    try:
        with open(zip_path, "rb") as f:
            zip_data = f.read()
        
        req_upload = urllib.request.Request(
            file_upload_url,
            data=zip_data,
            headers={"x-ms-blob-type": "BlockBlob"},
            method="PUT"
        )
        with urllib.request.urlopen(req_upload) as response:
            print("Upload completed successfully.")
    except urllib.error.HTTPError as e:
        print_http_error(e, "File Upload to Azure Blob")
        sys.exit(1)
    finally:
        if os.path.exists(zip_path):
            os.remove(zip_path)

    # 6. Update Submission Package List
    print("Updating flight submission packages data...")
    updated_packages = []

    # Set existing packages to PendingDelete
    if "flightPackages" in submission and submission["flightPackages"]:
        for pkg in submission["flightPackages"]:
            copied_pkg = {}
            # Safely copy properties
            for key, val in pkg.items():
                copied_pkg[key] = val
            copied_pkg["state"] = "PendingDelete"
            updated_packages.append(copied_pkg)

    # Add new package as PendingUpload
    new_package = {
        "fileName": msix_filename,
        "fileRequestName": msix_filename,
        "state": "PendingUpload"
    }
    updated_packages.append(new_package)
    submission["flightPackages"] = updated_packages

    # Send update PUT request
    update_url = f"{submission_url}/{submission_id}"
    try:
        req_update = urllib.request.Request(
            update_url,
            data=json.dumps(submission).encode("utf-8"),
            headers=headers,
            method="PUT"
        )
        with urllib.request.urlopen(req_update) as response:
            print("Flight submission packages updated successfully.")
    except urllib.error.HTTPError as e:
        print_http_error(e, "Update Submission Data")
        sys.exit(1)

    # 7. Commit Submission (POST)
    print("Committing the flight submission...")
    commit_url = f"{update_url}/commit"
    try:
        req_commit = urllib.request.Request(commit_url, headers=headers, method="POST")
        with urllib.request.urlopen(req_commit) as response:
            print("Flight submission committed successfully! Microsoft Store is now processing the package.")
    except urllib.error.HTTPError as e:
        print_http_error(e, "Commit Submission")
        sys.exit(1)

except Exception as global_err:
    print(f"\n[Fatal Error] An unexpected script error occurred:\n{global_err}", file=sys.stderr)
    import traceback
    traceback.print_exc(file=sys.stderr)
    sys.exit(1)
