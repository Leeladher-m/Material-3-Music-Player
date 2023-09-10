package com.omar.musica.ui.albumart

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.key.Keyer
import coil.request.Options
import coil.size.pxOrElse
import com.omar.musica.model.Song
import timber.log.Timber


class SongKeyer: Keyer<Song> {
    override fun key(data: Song, options: Options): String = data.uriString
}

class AlbumArtFetcher(
    private val data: Song,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? {


        Timber.d("AlbumArtFetcher request: " +
                "$data\n" +
                "${options.size}")

        val metadataRetriever = MediaMetadataRetriever()
            .apply { setDataSource(options.context, data.uriString.toUri()) }

        val byteArr = metadataRetriever.embeddedPicture ?: return null

        val bitmapOptions = BitmapFactory.Options()
            .apply {
                outWidth = options.size.height.pxOrElse { 0 }
                outHeight = options.size.width.pxOrElse { 0 }
            }
        val bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size, bitmapOptions) ?: return null
        try {
            metadataRetriever.release()
        } catch (e: Exception) { /**This method can throw for some reason*/}
        return DrawableResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = true,
            dataSource = DataSource.MEMORY
        )
    }


    class Factory : Fetcher.Factory<Song> {

        override fun create(data: Song, options: Options, imageLoader: ImageLoader): Fetcher {
            return AlbumArtFetcher(data, options)
        }
    }

}