package com.example.developerprofile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.developerprofile.ui.theme.DeveloperProfileTheme

data class ProjectUi(
    val id: Int,
    val title: String,
    val description: String,
    val technology: String
)

fun sampleProjects(): List<ProjectUi> = listOf(
    ProjectUi(
        id = 1,
        title = "Profil développeur",
        description = "Application Android présentant une fiche de profil développeur.",
        technology = "Kotlin, Jetpack Compose"
    ),
    ProjectUi(
        id = 2,
        title = "Product Explorer",
        description = "Application Android présentant des produits, leurs caractéristiques et leurs prix.",
        technology = "Kotlin, Material Design 3"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DeveloperProfileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfileContainer(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ProfileContainer(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        name = "Alex TANGUY",
        role = "Développeur Android junior",
        description = "Étudiant en informatique, passionné par le développement mobile.",
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onToggleFavoriteFilter = viewModel::onToggleFavoriteFilter,
        onFavoriteClick = viewModel::onFavoriteClick,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    name: String,
    role: String,
    description: String,
    uiState: ProfileUiState,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavoriteFilter: () -> Unit,
    onFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val skills = listOf("Kotlin", "Jetpack Compose", "Git", "Android Studio", "Compose UI")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
    ) {
        item {
            ProfileHeader(
                name = name,
                role = role,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }

        item {
            Text(text = "Compétences", style = MaterialTheme.typography.titleMedium)
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(skills) { skill -> SkillCard(skill = skill) }
            }
        }

        item {
            Text(text = "Mes projets", style = MaterialTheme.typography.titleMedium)
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Rechercher un projet") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(onClick = onToggleFavoriteFilter) {
                Text(
                    if (uiState.showOnlyFavorites) {
                        "Afficher tous les projets"
                    } else {
                        "Afficher uniquement les favoris"
                    }
                )
            }
        }

        item {
            Text(
                text = "${uiState.visibleProjects.size} projet(s) affiché(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(
            items = uiState.visibleProjects,
            key = { project -> project.id }
        ) { project ->
            ProjectCard(
                project = project,
                isFavorite = uiState.favoriteProjectIds.contains(project.id),
                onFavoriteClick = { onFavoriteClick(project.id) }
            )
        }

        if (uiState.visibleProjects.isEmpty()) {
            item {
                Text(
                    text = "Aucun projet correspondant.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            ContactDisclosure(
                availability = "Disponible pour un nouveau poste",
                email = "alex.tanguy@example.com"
            )
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    role: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Avatar de $name",
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "✓",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = role, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun SkillCard(
    skill: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = MaterialTheme.shapes.medium) {
        Text(
            text = skill,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectUi,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = project.title, style = MaterialTheme.typography.titleMedium)
            Text(text = project.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Technologies : ${project.technology}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            OutlinedButton(onClick = onFavoriteClick) {
                Text(if (isFavorite) "♥ Retirer des favoris" else "♡ Ajouter aux favoris")
            }
        }
    }
}

@Composable
fun ContactDisclosure(
    availability: String,
    email: String,
    modifier: Modifier = Modifier
) {
    var showContact by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProfileAction(
            label = if (showContact) "Masquer les informations de contact"
            else "Afficher les informations de contact",
            onClick = { showContact = !showContact }
        )
        if (showContact) {
            ContactInfo(availability = availability, email = email)
        }
    }
}

@Composable
fun ContactInfo(
    availability: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Contact", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_dialog_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(text = availability, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("Adresse e-mail") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_email),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ProfileAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(name = "Profil interactif - Clair", showBackground = true)
@Composable
fun ProfilePreview() {
    DeveloperProfileTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(
                name = "Alex TANGUY",
                role = "Développeur Android junior",
                description = "Étudiant en informatique, passionné par le développement mobile.",
                uiState = ProfileUiState(visibleProjects = sampleProjects()),
                onSearchQueryChange = {},
                onToggleFavoriteFilter = {},
                onFavoriteClick = {}
            )
        }
    }
}

@Preview(name = "Profil interactif - Sombre et favoris", showBackground = true)
@Composable
fun SecondProfilePreview() {
    DeveloperProfileTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ProfileScreen(
                name = "Camille Durand",
                role = "Développeuse Android confirmée",
                description = "Développeuse spécialisée dans la création d'applications Android modernes.",
                uiState = ProfileUiState(
                    visibleProjects = sampleProjects(),
                    searchQuery = "Kotlin",
                    showOnlyFavorites = true,
                    favoriteProjectIds = setOf(1, 2)
                ),
                onSearchQueryChange = {},
                onToggleFavoriteFilter = {},
                onFavoriteClick = {}
            )
        }
    }
}
