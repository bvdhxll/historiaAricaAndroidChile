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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import com.example.historiaarica.ui.theme.HistoriaAricaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

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
        "register" -> RegisterScreen(
            viewModel = viewModel,
            onRegisterSuccess = { currentScreen = "welcome" },
            onNavigateToLogin = { currentScreen = "welcome" }
        )
        "timeline" -> TimelineScreen(
            onNavigateToHome = { currentScreen = "welcome" },
            onNavigateToMap = { currentScreen = "timeline" },
            onNavigateToProfile = { currentScreen = "profile" }
        )
            "profile" -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    currentUser?.email?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            currentScreen = "welcome"
                        },
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .width(200.dp)
                    ) {
                        Text("Cerrar Sesión")
                    }
                    Button(
                        onClick = { currentScreen = "timeline" },
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .width(200.dp)
                    ) {
                        Text("Volver")
                    }
                }
            }
    }
}

@Composable
fun HistoryInfoScreen(viewModel: NombreViewModel) {
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
                painter = painterResource(R.drawable.morro_arica),
                contentDescription = "Imagen de bienvenida",
                modifier = Modifier.size(300.dp)
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

@Composable
fun RegisterScreen(
    viewModel: NombreViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var succesMessage by remember {mutableStateOf<String?>("")}
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
                                        succesMessage = "Registrado correctamente"
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
            Text("\uD835\uDD7D\uD835\uDD8A\uD835\uDD8C\uD835\uDD8E\uD835\uDD98\uD835\uDD99\uD835\uDD97\uD835\uDD86\uD835\uDD97\uD835\uDD98\uD835\uDD8A")
        }

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = Color.Red)
        }

        succesMessage?.let {
            if (it.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = Color.Green)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Línea de Tiempo", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onNavigateToHome
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Place, contentDescription = "Mapa") },
                    label = { Text("Mapa") },
                    selected = true,
                    onClick = onNavigateToMap
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Mi Cuenta") },
                    label = { Text("Mi Cuenta") },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateToHome: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            currentUser?.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}