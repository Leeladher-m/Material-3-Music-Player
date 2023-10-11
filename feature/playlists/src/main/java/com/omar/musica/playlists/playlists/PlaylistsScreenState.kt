package com.omar.musica.playlists.playlists

import com.omar.musica.model.PlaylistInfo

sealed interface PlaylistsScreenState {

    data object Loading : PlaylistsScreenState
    data class Success(val playlists: List<PlaylistInfo>) : PlaylistsScreenState

}