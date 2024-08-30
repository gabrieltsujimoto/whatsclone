package com.gabrieltsujimoto.whatsappclone.model

data class Usuario(
    var uid: String,
    var name: String,
    var photo: String = "",
    var mail: String,
)
