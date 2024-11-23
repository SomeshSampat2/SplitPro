package com.example.splitpro.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitpro.R
import com.example.splitpro.auth.AuthState
import com.example.splitpro.auth.AuthViewModel
import com.example.splitpro.common.components.GoogleSignInButton
import com.example.splitpro.ui.theme.Primary
import com.example.splitpro.ui.theme.PrimaryLight
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun LoginScreen(
    onSignInClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val authState by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Configure Google Sign In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(context, task)
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.SignedIn -> {
                if ((authState as AuthState.SignedIn).shouldNavigateToProfile) {
                    onNavigateToProfile()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface,
                        PrimaryLight.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        // Top Wave Pattern
        Image(
            painter = painterResource(id = R.drawable.wave_pattern),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "SplitPro Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Name
            Text(
                text = "SplitPro",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tagline
            Text(
                text = "Split expenses with friends,\neffortlessly",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Features List
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureItem(
                    icon = R.drawable.ic_group,
                    text = "Create groups for different occasions"
                )
                FeatureItem(
                    icon = R.drawable.ic_receipt,
                    text = "Track expenses and split bills easily"
                )
                FeatureItem(
                    icon = R.drawable.ic_payment,
                    text = "Settle up with a single tap"
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign In Button with increased elevation
            GoogleSignInButton(
                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Primary.copy(alpha = 0.25f)
                    )
                    .zIndex(1f),
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error Message
        AnimatedVisibility(
            visible = authState is AuthState.Error,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Loading Indicator
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = Primary
            )
        }
    }
}

@Composable
private fun FeatureItem(
    @DrawableRes icon: Int,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
