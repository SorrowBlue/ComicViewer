const https = require('https');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

function request(options, data = null) {
    return new Promise((resolve, reject) => {
        const req = https.request(options, (res) => {
            let body = '';
            res.on('data', chunk => body += chunk);
            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    resolve(body ? JSON.parse(body) : null);
                } else {
                    reject(new Error(`Request to ${options.path} failed with status ${res.statusCode}: ${body}`));
                }
            });
        });
        req.on('error', reject);
        if (data) {
            req.write(typeof data === 'string' ? data : JSON.stringify(data));
        }
        req.end();
    });
}

function uploadFile(url, filePath) {
    return new Promise((resolve, reject) => {
        const stats = fs.statSync(filePath);
        const parsedUrl = new URL(url);
        const options = {
            hostname: parsedUrl.hostname,
            port: parsedUrl.port || 443,
            path: parsedUrl.pathname + parsedUrl.search,
            method: 'PUT',
            headers: {
                'x-ms-blob-type': 'BlockBlob',
                'Content-Length': stats.size
            }
        };
        const req = https.request(options, (res) => {
            if (res.statusCode === 200 || res.statusCode === 201) {
                resolve();
            } else {
                let body = '';
                res.on('data', chunk => body += chunk);
                res.on('end', () => reject(new Error(`Upload failed with status ${res.statusCode}: ${body}`)));
            }
        });
        req.on('error', reject);
        const stream = fs.createReadStream(filePath);
        stream.pipe(req);
    });
}

function findMsixFile(dir) {
    const files = fs.readdirSync(dir);
    const msixFiles = files.filter(f => f.endsWith('.msix'));
    if (msixFiles.length === 0) {
        throw new Error(`No .msix files found in directory: ${dir}`);
    }
    if (msixFiles.length > 1) {
        console.log(`Warning: Multiple .msix files found in ${dir}. Using the first one: ${msixFiles[0]}`);
    }
    return path.join(dir, msixFiles[0]);
}

function createZipArchive(msixPath, zipPath) {
    const msixDir = path.dirname(msixPath);
    const msixName = path.basename(msixPath);
    const absoluteZipPath = path.resolve(zipPath);

    console.log(`Creating ZIP archive: ${absoluteZipPath} containing ${msixName}...`);

    if (process.platform === 'win32') {
        const cmd = `powershell.exe -Command "Compress-Archive -Path '${msixPath}' -DestinationPath '${absoluteZipPath}' -Force"`;
        execSync(cmd, { stdio: 'inherit' });
    } else {
        const cmd = `zip -j "${absoluteZipPath}" "${msixPath}"`;
        execSync(cmd, { stdio: 'inherit' });
    }
    console.log("ZIP archive created successfully.");
}

async function main() {
    const tenantId = process.env.INPUT_TENANTID;
    const clientId = process.env.INPUT_CLIENTID;
    const clientSecret = process.env.INPUT_CLIENTSECRET;
    const appId = process.env.INPUT_APPID;
    const flightId = process.env.INPUT_FLIGHTID;
    const packageDirectory = process.env.INPUT_PACKAGEDIRECTORY || 'output';

    const msixPath = findMsixFile(packageDirectory);
    const msixName = path.basename(msixPath);
    console.log(`Found MSIX package: ${msixPath}`);

    const tempZipPath = path.join(packageDirectory, 'temp_upload.zip');

    try {
        createZipArchive(msixPath, tempZipPath);

        console.log("Authenticating with Microsoft Entra ID...");
        const authData = `grant_type=client_credentials&client_id=${clientId}&client_secret=${encodeURIComponent(clientSecret)}&resource=https://manage.devcenter.microsoft.com`;
        const tokenResponse = await request({
            hostname: 'login.microsoftonline.com',
            path: `/${tenantId}/oauth2/token`,
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Content-Length': Buffer.byteLength(authData)
            }
        }, authData);
        const token = tokenResponse.access_token;
        console.log("Authentication successful.");

        const baseHeaders = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        const isFlight = !!flightId;
        const basePath = isFlight 
            ? `/v1.0/my/applications/${appId}/flights/${flightId}`
            : `/v1.0/my/applications/${appId}`;

        console.log(`Checking for active submissions (Flight: ${isFlight})...`);
        
        let submissionId;
        let fileUploadUrl;
        let submissionData;

        try {
            const response = await request({
                hostname: 'manage.devcenter.microsoft.com',
                path: `${basePath}/submissions`,
                method: 'POST',
                headers: baseHeaders
            });
            submissionId = response.id;
            fileUploadUrl = response.fileUploadUrl;
            submissionData = response;
            console.log(`Created new submission draft: ${submissionId}`);
        } catch (e) {
            if (e.message.includes("An active submission already exists") || e.message.includes("SubmissionAlreadyExists")) {
                console.log("An active submission already exists. Retrieving existing submission...");
                const appOrFlightData = await request({
                    hostname: 'manage.devcenter.microsoft.com',
                    path: basePath,
                    method: 'GET',
                    headers: baseHeaders
                });
                
                const pendingSubmission = isFlight 
                    ? appOrFlightData.pendingFlightSubmission 
                    : appOrFlightData.pendingApplicationSubmission;

                if (pendingSubmission) {
                    submissionId = pendingSubmission.id;
                    console.log(`Found pending submission: ${submissionId}`);
                } else {
                    throw new Error("Could not find pending submission ID despite API indicating one exists.");
                }

                submissionData = await request({
                    hostname: 'manage.devcenter.microsoft.com',
                    path: `${basePath}/submissions/${submissionId}`,
                    method: 'GET',
                    headers: baseHeaders
                });
                fileUploadUrl = submissionData.fileUploadUrl;
            } else {
                throw e;
            }
        }

        console.log(`Uploading package ZIP (${tempZipPath}) to Azure Blob Storage...`);
        await uploadFile(fileUploadUrl, tempZipPath);
        console.log("Package ZIP uploaded successfully.");

        console.log("Updating submission package lists...");
        if (isFlight) {
            const currentPackages = submissionData.flightPackages || [];
            const updatedPackages = currentPackages.map(pkg => ({
                ...pkg,
                fileStatus: 'PendingDelete'
            }));
            updatedPackages.push({
                fileName: msixName,
                fileStatus: 'PendingUpload',
                minimumDirectXVersion: 'None',
                minimumSystemRam: 'None'
            });
            submissionData.flightPackages = updatedPackages;
            delete submissionData.applicationPackages;
        } else {
            const currentPackages = submissionData.applicationPackages || [];
            const updatedPackages = currentPackages.map(pkg => ({
                ...pkg,
                fileStatus: 'PendingDelete'
            }));
            updatedPackages.push({
                fileName: msixName,
                fileStatus: 'PendingUpload',
                minimumDirectXVersion: 'None',
                minimumSystemRam: 'None'
            });
            submissionData.applicationPackages = updatedPackages;
            delete submissionData.flightPackages;
        }

        await request({
            hostname: 'manage.devcenter.microsoft.com',
            path: `${basePath}/submissions/${submissionId}`,
            method: 'PUT',
            headers: baseHeaders
        }, submissionData);
        console.log("Submission packages updated.");

        console.log("Committing submission for certification...");
        await request({
            hostname: 'manage.devcenter.microsoft.com',
            path: `${basePath}/submissions/${submissionId}/commit`,
            method: 'POST',
            headers: baseHeaders
        });
        console.log("Submission committed successfully!");
    } finally {
        if (fs.existsSync(tempZipPath)) {
            console.log(`Cleaning up temporary ZIP file: ${tempZipPath}`);
            fs.unlinkSync(tempZipPath);
        }
    }
}

main().catch(err => {
    console.error("Critical error in msstore-publish action:", err);
    process.exit(1);
});
