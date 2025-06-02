# 🔐 KeycloakLoginDemo

This is a simple Android demo project for logging in with **Keycloak**, using the [AppAuth](https://github.com/openid/AppAuth-Android) library.

---

## ✅ Features

- Login with **Keycloak**
- Retrieve the `access_token` after successful login
- Based on the standard OpenID Connect (OIDC) flow using `AppAuth`

---

## ⚙️ Requirements

- A working **Keycloak server** (local or hosted)
- A configured **Client** in Keycloak with the following:
  - Type: `public`
  - Client ID: `android-client` (or any name you prefer)
  - Redirect URI:
    ```
    ir.hajkarami.keycloaklogindemo://oauth2redirect
    ```

---

## 🧩 Dependency

In your `build.gradle (app)` file, add:

```groovy
implementation 'net.openid:appauth:0.11.1'

🧠 How It Works
1. Configure the Authorization Service

val serviceConfig = AuthorizationServiceConfiguration(
    Uri.parse("http://10.0.2.2:8080/realms/myapp/protocol/openid-connect/auth"),
    Uri.parse("http://10.0.2.2:8080/realms/myapp/protocol/openid-connect/token")
)

2. Create the Authorization Request

val request = AuthorizationRequest.Builder(
    serviceConfig,
    clientId,
    ResponseTypeValues.CODE,
    redirectUri
).setScope("openid profile email")
 .build()

3. Launch the Login Page

val intent = authService.getAuthorizationRequestIntent(request)
startActivityForResult(intent, 100)

4. Handle the Result and Get the Access Token
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == 100) {
        val resp = AuthorizationResponse.fromIntent(data!!)
        val tokenRequest = resp?.createTokenExchangeRequest()

        if (tokenRequest != null) {
            authService.performTokenRequest(tokenRequest) { response, exception ->
                if (response != null) {
                    Log.d("KEYCLOAK", "Access token: ${response.accessToken}")
                }
            }
        }
    }
}

⚠️ Important Notes
When using the Android emulator, use http://10.0.2.2 instead of localhost.

Make sure the redirect URI matches exactly in both the Android code and Keycloak client settings.

This project only covers login and token exchange. You can later use the token to access the userinfo endpoint.

🧑‍💻 Developer
MohamadSaleh HajKarami



