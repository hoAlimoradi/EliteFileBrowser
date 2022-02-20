package com.alimoradi.elitefilebrowser.note

interface NoteManager {

    fun getNote(): String

    fun setNote(text: String)

    fun delete()

    fun share()
}
