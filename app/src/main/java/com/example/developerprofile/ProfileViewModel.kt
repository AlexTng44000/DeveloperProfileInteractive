package com.example.developerprofile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val visibleProjects: List<ProjectUi> = emptyList(),
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val favoriteProjectIds: Set<Int> = emptySet()
)

class ProfileViewModel : ViewModel() {
    private val allProjects = sampleProjects()

    private val _uiState = MutableStateFlow(
        ProfileUiState(visibleProjects = allProjects)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                visibleProjects = filterProjects(
                    query = query,
                    onlyFavorites = current.showOnlyFavorites,
                    favorites = current.favoriteProjectIds
                )
            )
        }
    }

    fun onToggleFavoriteFilter() {
        _uiState.update { current ->
            val showFavorites = !current.showOnlyFavorites
            current.copy(
                showOnlyFavorites = showFavorites,
                visibleProjects = filterProjects(
                    query = current.searchQuery,
                    onlyFavorites = showFavorites,
                    favorites = current.favoriteProjectIds
                )
            )
        }
    }

    fun onFavoriteClick(projectId: Int) {
        _uiState.update { current ->
            val favorites = if (projectId in current.favoriteProjectIds) {
                current.favoriteProjectIds - projectId
            } else {
                current.favoriteProjectIds + projectId
            }
            current.copy(
                favoriteProjectIds = favorites,
                visibleProjects = filterProjects(
                    query = current.searchQuery,
                    onlyFavorites = current.showOnlyFavorites,
                    favorites = favorites
                )
            )
        }
    }

    private fun filterProjects(
        query: String,
        onlyFavorites: Boolean,
        favorites: Set<Int>
    ): List<ProjectUi> = allProjects.filter { project ->
        val matchesSearch =
            project.title.contains(query, ignoreCase = true) ||
                    project.technology.contains(query, ignoreCase = true) ||
                    project.description.contains(query, ignoreCase = true)

        val matchesFavorite = !onlyFavorites || project.id in favorites
        matchesSearch && matchesFavorite
    }
}
