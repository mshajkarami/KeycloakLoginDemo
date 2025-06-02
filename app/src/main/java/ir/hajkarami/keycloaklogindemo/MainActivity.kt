package ir.hajkarami.keycloaklogindemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.openid.appauth.*
import net.openid.appauth.connectivity.ConnectionBuilder

import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var authService: AuthorizationService
    private val TAG = "KEYCLOAK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val appAuthConfig = AppAuthConfiguration.Builder()
            .setConnectionBuilder(object : ConnectionBuilder {
                override fun openConnection(uri: Uri): HttpURLConnection {
                    val connection = URL(uri.toString()).openConnection()
                    if (connection !is HttpURLConnection) {
                        throw IllegalArgumentException("Expected HTTP connection")
                    }
                    return connection
                }
            })
            .build()


        authService = AuthorizationService(this,appAuthConfig)

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("http://10.0.2.2:8080/realms/myapp/protocol/openid-connect/auth"),
            Uri.parse("http://10.0.2.2:8080/realms/myapp/protocol/openid-connect/token")
        )
        val clientId = "android-client"
        val redirectUri = Uri.parse("ir.hajkarami.keycloaklogindemo://oauth2redirect")

        val request = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        ).setScope("openid profile email")
            .build()

        val intent = authService.getAuthorizationRequestIntent(request)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            data?.let {
                val resp = AuthorizationResponse.fromIntent(it)
                val ex = AuthorizationException.fromIntent(it)

                if (resp != null) {
                    val tokenRequest = resp.createTokenExchangeRequest()
                    authService.performTokenRequest(tokenRequest) { response, exception ->
                        if (response != null) {
                            Log.d(TAG, "Access token: ${response.accessToken}")
                        } else {
                            Log.e(TAG, "Token exchange failed", exception)
                        }
                    }
                } else {
                    Log.e(TAG, "Auth failed", ex)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}
