class GoogleDriveUtils {

    SCOPES = 'https://www.googleapis.com/auth/drive.file';
    DOCUMENT_TYPE = 'application/vnd.oasis.opendocument.text';
    GOOGLE_TYPE = 'application/vnd.google-apps.document';

    clientId = null;
    enabled = false;
    credential = null;
    access_token = null;
    drive_file = null;

    constructor(googleClientId, enabled) {
        this.clientId = googleClientId;
        this.enabled = enabled;
        if (location.hostname !== 'localhost' && location.protocol === 'http:'  ) {
            console.log('Google sign works only for localhost or https');
            this.enabled = false;
        }
        window.gsiLoggedIn = this.loggedIn; // callback from gsi widget in home
    }

    loggedIn = (response) => {
        this.credential = this.parseJwt(response.credential);
    }

    parseJwt(token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    }

    async accessUploadAndShow(url, name) {
        if (this.enabled) {
            blockUI();
            if (!this.access_token) {
                const tokenClient = google.accounts.oauth2.initTokenClient({
                    client_id: this.clientId,
                    scope: this.SCOPES,
                    hint: this.credential ? this.credential.email : undefined,
                    callback: async (resp) => {
                        if (resp.error !== undefined) {
                            throw (resp);
                        }
                        this.access_token = resp.access_token;

                        const file = await alfresco.get(url);
                        this.upload(file, name);
                    }
                });
                tokenClient.requestAccessToken({ prompt: '' });
            } else {
                const file = await alfresco.get(url);
                this.upload(file, name);
            }
        }
    }

    async upload(file, name) {
        blockUI();
        const metadata = {
            name: `${name}.odt`,
            mimeType: this.GOOGLE_TYPE,
            // parents: [parent_Folder_id]
        };
        const formData = new FormData();
        formData.append("metadata", new Blob([JSON.stringify(metadata)], { type: 'application/json' }));
        formData.append("file", file);
        const response = await fetch("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=webViewLink,webContentLink,id,name", {
            method: 'POST',
            headers: new Headers({ "Authorization": "Bearer " + this.access_token }),
            body: formData
        });
        if (response.ok) {
        	this.drive_file = await response.json();
        } else {
        	alert(await response.text());
        }
        blockUI();
        const iframe = document.getElementById('editorFrame');
        iframe.onload = () => { 
            unblockUI();
            iframe.onload = undefined;
        };

        iframe.src = this.drive_file.webViewLink;
    }

    async download() {
        blockUI();
        const response = await fetch(`https://www.googleapis.com/drive/v3/files/${this.drive_file.id}/export?mimeType=${this.DOCUMENT_TYPE}`, {
            method: 'GET',
            headers: new Headers({ "Authorization": "Bearer " + this.access_token }),
        });
        const body = await response.blob();

        alfresco.save(null, body, /*editor*/ null);
        unblockUI();
    }

    async delete() {
        blockUI();
        if (this.enabled) {
            await fetch(`https://www.googleapis.com/drive/v3/files/${this.drive_file.id}`, {
                method: 'DELETE',
                headers: new Headers({ "Authorization": "Bearer " + this.access_token }),
            });
        }
        unblockUI();
    }

}
