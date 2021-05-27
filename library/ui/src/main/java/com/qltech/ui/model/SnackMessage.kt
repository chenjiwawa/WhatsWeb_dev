package com.qltech.ui.model

data class SnackMessage(
    val type: Type,
    val message: String,
) {

    enum class Type {
        NORMAL,
        ERROR
    }
}