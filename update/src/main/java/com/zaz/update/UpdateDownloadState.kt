package com.zaz.update

enum class UpdateDownloadState {
    IDLE,
    DOWNLOADING,
    PAUSED,
    CANCELED,
    FAILED,
    SUCCEED
}