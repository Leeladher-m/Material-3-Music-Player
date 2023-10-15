package com.omar.musica.ui.common

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.omar.musica.ui.R
import com.omar.musica.ui.albumart.LocalEfficientThumbnailImageLoader
import com.omar.musica.ui.albumart.LocalInefficientThumbnailImageLoader
import com.omar.musica.ui.model.SongUi
import timber.log.Timber


@Composable
fun SelectableSongRow(
    modifier: Modifier,
    song: SongUi,
    menuOptions: List<MenuActionItem>? = null,
    multiSelectOn: Boolean = false,
    isSelected: Boolean = false,
    efficientThumbnailLoading: Boolean = true,
) {

    Row(
        modifier = modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(6.dp)),
            model = song,
            imageLoader = if (efficientThumbnailLoading) LocalEfficientThumbnailImageLoader.current else LocalInefficientThumbnailImageLoader.current,
            contentDescription = "Cover Photo",
            contentScale = ContentScale.Crop,
            fallback = rememberVectorPainter(image = Icons.Rounded.MusicNote),
            placeholder = painterResource(id = R.drawable.placeholder),
            error = painterResource(id = R.drawable.placeholder),
            onError = { Timber.d("uri: ${it.result.request.data}" + it.result.throwable.stackTraceToString()) }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {

            Text(
                text = song.title,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            //Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = song.artist.toString(),
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = song.album.toString(),
                    fontSize = 11.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = song.length.millisToTime(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }


        }


        Box(
            Modifier
                .fillMaxHeight()
                .width(48.dp), contentAlignment = Alignment.Center) {

            if (menuOptions != null) {
                androidx.compose.animation.AnimatedVisibility(visible = !multiSelectOn, enter = EnterTransition.None, exit = ExitTransition.None) {
                    SongOverflowMenu(menuOptions = menuOptions)
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = multiSelectOn && isSelected,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)),
                exit = scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

        }


    }

}

@Composable
fun SongOverflowMenu(menuOptions: List<MenuActionItem>) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
    }
    SongDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        actions = menuOptions
    )
}