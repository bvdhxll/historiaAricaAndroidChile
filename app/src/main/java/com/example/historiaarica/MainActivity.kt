package com.example.historiaarica.Data

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
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
import com.google.android.gms.maps.CameraUpdateFactory

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
    var selectedPosition by remember { mutableStateOf<LatLng?>(null) }

        when (currentScreen) {
        "welcome" -> WelcomeScreen(
            onNavigateToInfo = { currentScreen = "info" },
            onNavigateToRegister = { currentScreen = "register" },
            onLoginSuccess = { currentScreen = "home" }
        )
        "register" -> RegisterScreen(
            viewModel = viewModel,
            onRegisterSuccess = { currentScreen = "welcome" },
            onNavigateToLogin = { currentScreen = "welcome" }
        )
            "timeline" -> TimelineScreen(
                onNavigateToHome = { currentScreen = "home" },
                onNavigateToMap = { currentScreen = "timeline" },
                onNavigateToProfile = { currentScreen = "profile" },
                initialPosition = selectedPosition ?: LatLng(-18.4746, -70.2979)
            )
            "home" -> HistoryInfoScreen(
                onNavigateToProfile = { currentScreen = "profile" },
                onNavigateToTimeline = { position ->
                    selectedPosition = position
                    currentScreen = "timeline"
                }
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
                        onClick = { currentScreen = "home" },
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

data class HistoricPlace(
    val name: String,
    val position: LatLng,
    val description: String
)

val historicPlaces = listOf(
    HistoricPlace(
        "Morro de Arica",
        LatLng(-18.480277777778, -70.323611111111),
        "Histórico morro de la ciudad"
    ),
    HistoricPlace(
        "Catedral San Marcos",
        LatLng(-18.478951, -70.320725),
        "Catedral histórica de Arica"
    ),
    HistoricPlace(
        "Ex Aduana",
        LatLng(-18.47715278, -70.32103611),
        "Antigua aduana de la ciudad"
    ),
    HistoricPlace(
        "Plaza Colón",
        LatLng(-18.4778438, -70.3181884),
        "Plaza principal de la ciudad"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryInfoScreen(
    onNavigateToTimeline: (LatLng) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Explora la historia de Arica", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = "home",
                onNavigateToHome = {  },
                onNavigateToMap = { onNavigateToTimeline(LatLng(-18.4746, -70.2979)) },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(historicPlaces) { place ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Button(
                            onClick = { onNavigateToTimeline(place.position) }
                        ) {
                            Text("Explorar")
                        }
                    }
                }
            }
        }
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
    onNavigateToProfile: () -> Unit,
    initialPosition: LatLng = LatLng(-18.4746, -70.2979)
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 15f)
    }

    LaunchedEffect(initialPosition) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(initialPosition, 15f)
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mapa de Arica", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = "timeline",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMap = onNavigateToMap,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false
                )
            ) {
                historicPlaces.forEach { place ->
                    Marker(
                        state = MarkerState(position = place.position),
                        title = place.name,
                        snippet = place.description
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigateToHome: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentScreen == "home",
            onClick = onNavigateToHome
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Place, contentDescription = "Mapa") },
            label = { Text("Mapa") },
            selected = currentScreen == "timeline",
            onClick = onNavigateToMap
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Mi Cuenta") },
            label = { Text("Mi Cuenta") },
            selected = currentScreen == "profile",
            onClick = onNavigateToProfile
        )
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