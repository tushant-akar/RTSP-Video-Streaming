package com.tushant.rtspvideostreaming.di

import androidx.media3.exoplayer.ExoPlayer
import com.tushant.rtspvideostreaming.viewModel.VideoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ExoPlayer.Builder(androidContext()).build() }
    viewModel { VideoViewModel(get(), androidContext()) }
}