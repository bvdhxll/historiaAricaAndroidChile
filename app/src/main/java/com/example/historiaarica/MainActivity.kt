package com.example.historiaarica.Data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.historiaarica.Data.NombreViewModel
import com.example.historiaarica.ui.theme.HistoriaAricaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: NombreViewModel by viewModels()

        setContent {
            HistoriaAricaTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: NombreViewModel) {
    var currentScreen by remember { mutableStateOf("welcome") }

    when (currentScreen) {
        "welcome" -> WelcomeScreen(
            onNavigateToInfo = { currentScreen = "info" },
            onNavigateToRegister = { currentScreen = "register" },
            onLoginSuccess = { currentScreen = "timeline" }
        )
        "info" -> HistoryInfoScreen(viewModel)
        "register" -> RegisterScreen(viewModel, onRegisterSuccess = { currentScreen = "welcome" })
        "timeline" -> TimelineScreen()
    }
}

@Composable
fun HistoryInfoScreen(viewModel: NombreViewModel) {
    // Implementación para mostrar información histórica
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Información histórica", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Aquí puedes explorar eventos históricos de Arica.")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onNavigateToInfo: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("\uD835\uDD73\uD835\uDD8E\uD835\uDD98\uD835\uDD99\uD835\uDD94\uD835\uDD97\uD835\uDD8E\uD835\uDD86 \uD835\uDD6F\uD835\uDD8A \uD835\uDD6C\uD835\uDD97\uD835\uDD8E\uD835\uDD88\uD835\uDD86", fontWeight = FontWeight.Bold, fontSize = 50.sp) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(android.R.drawable.ic_menu_mapmode),
                contentDescription = "Imagen de bienvenida",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "¡Explora la historia de Arica!",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                errorMessage = "Error de inicio de sesión: ${task.exception?.message}"
                            }
                        }
                },
                modifier = Modifier.width(140.dp)


            ) {
                Text("\uD835\uDD74\uD835\uDD93\uD835\uDD8C\uD835\uDD97\uD835\uDD8A\uD835\uDD98\uD835\uDD86\uD835\uDD97")
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier.width(140.dp)

            ) {
                Text("\uD835\uDD7D\uD835\uDD8A\uD835\uDD8C\uD835\uDD8E\uD835\uDD98\uD835\uDD99\uD835\uDD97\uD835\uDD86\uD835\uDD97\uD835\uDD98\uD835\uDD8A")

            }

        }
    }
}

fun Text(color: Color) {

}

@Composable
fun RegisterScreen(viewModel: NombreViewModel, onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Pantalla de Registro",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password == confirmPassword) {
                    val auth = FirebaseAuth.getInstance()
                    val db = FirebaseFirestore.getInstance()

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = hashMapOf(
                                    "email" to email,
                                    "created_at" to System.currentTimeMillis()
                                )
                                db.collection("users")
                                    .document(auth.currentUser!!.uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        onRegisterSuccess()
                                    }
                                    .addOnFailureListener {
                                        errorMessage = "Error al guardar en Firestore: ${it.message}"
                                    }
                            } else {
                                errorMessage = "Error de registro: ${task.exception?.message}"
                            }
                        }
                } else {
                    errorMessage = "Las contraseñas no coinciden o el email está vacío"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = Color.Red)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Línea de Tiempo", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Explora los eventos históricos de Arica",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


