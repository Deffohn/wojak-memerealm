package fr.stks.wojakmemesrealm.model

import android.util.Log

class User {

    constructor()

    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""

    constructor(username: String, fullname: String, bio: String, image: String, uid: String){
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
    }

    fun getUsername(): String{
        return this.username
    }

    fun setUsername(username: String){
        this.username = username
    }

    fun getFullname(): String{
        return this.fullname
    }

    fun setFullname(fullname: String){
        this.fullname = fullname
    }

    fun getBio(): String{
        return this.bio
    }

    fun setBio(bio: String){
        this.bio = bio
    }

    fun getImage(): String{
        return this.image
    }

    fun setImage(image: String){
        this.image = image
    }

    fun getUid(): String{
        Log.d("Debug User", this.uid+" ")
        return this.uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

}