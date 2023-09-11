package com.omar.nowplaying.ui

import BlurTransformation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.omar.musica.ui.albumart.LocalThumbnailImageLoader
import com.omar.musica.ui.common.millisToTime
import com.omar.nowplaying.NowPlayingState
import com.omar.nowplaying.viewmodel.NowPlayingViewModel
import timber.log.Timber


@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val uiState by viewModel.currentPlayingSong.collectAsState()
    NowPlayingScreen(
        uiState = uiState,
        onUserSeek = viewModel::onUserSeek
    )
}

@Composable
internal fun NowPlayingScreen(
    uiState: NowPlayingState,
    onUserSeek: (Float) -> Unit
) {
    if (uiState.song == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Null Song")
        }
        return
    }


    val imageLoader = LocalThumbnailImageLoader.current
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(context)
                .crossfade(500)
                .data(uiState.song)
                .transformations(
                    BlurTransformation(radius = 40, scale = 0.15f)
                ).build(),
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            onError = { Timber.e(it.result.throwable) },
            error = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            colorFilter = ColorFilter.tint(
                Color(0xFF999999),
                BlendMode.Multiply
            ) // darken the blur a bit
        )

        NowPlayingUi(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                .statusBarsPadding(),
            uiState = uiState,
            onUserSeek = onUserSeek,
        )

    }
}

@Composable
fun NowPlayingUi(
    modifier: Modifier,
    uiState: NowPlayingState,
    onUserSeek: (Float) -> Unit
) {

    Column(modifier) {

        val imageLoader = LocalThumbnailImageLoader.current
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f)
                .scale(0.9f)
                .shadow(32.dp)
                .clip(RoundedCornerShape(12.dp)),
            model = uiState.song,
            contentDescription = "Artwork",
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = uiState.song?.title ?: "No Playing Song",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SongProgressInfo(
            modifier = Modifier.fillMaxWidth(),
            songDuration = uiState.song?.length ?: 0L,
            progress = uiState.songProgress,
            onUserSeek = onUserSeek
        )


    }


}

@Composable
fun SongProgressInfo(
    modifier: Modifier,
    songDuration: Long,
    progress: Float,
    onUserSeek: (progress: Float) -> Unit
) {

    val currentTimestamp = (songDuration * progress).toLong().millisToTime()
    val songLength = songDuration.millisToTime()
    var sliderValue by remember {
        mutableFloatStateOf(progress)
    }

    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = currentTimestamp,
            modifier = Modifier.width(50.dp),
            fontSize = 9.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light
        )
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp, end = 4.dp),
            enabled = true,
            onValueChangeFinished = { onUserSeek(sliderValue) },
        )
        Text(
            text = songLength,
            modifier = Modifier.width(50.dp),
            fontSize = 9.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light
        )

    }


}