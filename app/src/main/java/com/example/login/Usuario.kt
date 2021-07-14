package com.example.login

data class Usuario(
    var nombre: String,
    var apellidos:String,
    var edad: Int,
    var profesion:String
){
    constructor() : this("","",0,"")
}
