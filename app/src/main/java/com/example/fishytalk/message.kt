package com.example.fishytalk

class message {
    var msg:String? = null
    var senderId:String? = null

    constructor(){}

    constructor( msg:String? , senderId:String? ){
        this.msg = msg
        this.senderId = senderId
    }

}