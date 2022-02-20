package com.alimoradi.elitefilebrowser.addAccount

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.alimoradi.elitefilebrowser.R
import com.alimoradi.elitefilebrowser.data.AppPreferences

class UserAdapter(val context:Context,
                  val personSignalProtocolAsJsonList:ArrayList<PersonSignalProtocolAsJsonData>
                  /*,
                  val onClickListener: OnClickListener*/):RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var selectedItemPosition = -1
    var lastItemSelectedPos = -1


  inner class UserViewHolder(val v:View):RecyclerView.ViewHolder(v){


      var parentRowUserSignal: CardView = v.findViewById<CardView>(R.id.parentRowUserSignal)
      var checkbox: AppCompatCheckBox = v.findViewById<AppCompatCheckBox>(R.id.checkbox)
      var name: TextView
      var store:TextView
      var preKey:TextView
      var address:TextView


      var edit:ImageView
      var delete:ImageView

      init {
          name = v.findViewById<TextView>(R.id.name)
          store = v.findViewById<TextView>(R.id.store)
          preKey = v.findViewById<TextView>(R.id.preKey)
          address = v.findViewById<TextView>(R.id.address)

          edit = v.findViewById(R.id.editValue)
          delete = v.findViewById(R.id.deleteValue)
          edit.setOnClickListener { edit(it) }
          delete.setOnClickListener { delete(it) }

          parentRowUserSignal.setOnClickListener {
              selectedItemPosition = adapterPosition
              if(lastItemSelectedPos == -1)
                  lastItemSelectedPos = selectedItemPosition
              else {
                  notifyItemChanged(lastItemSelectedPos)
                  lastItemSelectedPos = selectedItemPosition
              }
              notifyItemChanged(selectedItemPosition)
          }
      }

      fun defaultBg() {
          parentRowUserSignal.background = context.getDrawable(R.drawable.bg_capsule_unselected)
      }

      fun selectedBg() {
          parentRowUserSignal.background = context.getDrawable(R.drawable.bg_capsule_selected)
      }
      private fun delete(v:View) {
          val position = personSignalProtocolAsJsonList[adapterPosition]
          AlertDialog.Builder(context)
              .setTitle("Delete")
              .setIcon(R.drawable.ic_warning)
              .setMessage("Are you sure delete this Information")
              .setPositiveButton("Yes"){
                      dialog,_->
                  personSignalProtocolAsJsonList.removeAt(adapterPosition)
                  notifyDataSetChanged()
                  AppPreferences.setUserSignalProtocolDataList(personSignalProtocolAsJsonList)
                  Toast.makeText(context,"Deleted this Information",Toast.LENGTH_SHORT).show()
                  dialog.dismiss()
              }
              .setNegativeButton("No"){
                      dialog,_->
                  dialog.dismiss()
              }
              .create()
              .show()
      }

      private fun edit(v:View) {
          val position = personSignalProtocolAsJsonList[adapterPosition]

          val v = LayoutInflater.from(context).inflate(R.layout.add_account_item,null)
          val name = v.findViewById<EditText>(R.id.name)
          val deviceId = v.findViewById<EditText>(R.id.deviceId)
          val signedPreKeyId = v.findViewById<EditText>(R.id.signedPreKeyId)
          val registrationId = v.findViewById<EditText>(R.id.registrationId)
          val identityKeyPair = v.findViewById<EditText>(R.id.identityKeyPair)
          val preKeys = v.findViewById<EditText>(R.id.preKeys)
          val signedPreKey = v.findViewById<EditText>(R.id.signedPreKey)
          val address = v.findViewById<EditText>(R.id.address)

          AlertDialog.Builder(context)
              .setView(v)
              .setPositiveButton("Ok"){
                      dialog,_->
                  position.name = name.text.toString()
                  position.deviceId = deviceId.text.toString().toInt()
                  position.signedPreKeyId = signedPreKeyId.text.toString().toInt()
                  position.registrationId = registrationId.text.toString().toInt()
                  position.identityKeyPair = identityKeyPair.text.toString()
                  position.preKeys = preKeys.text.toString()
                  position.signedPreKey = signedPreKey.text.toString()
                  position.address = address.text.toString()

                  notifyDataSetChanged()
                  AppPreferences.setUserSignalProtocolDataList(personSignalProtocolAsJsonList)
                  Toast.makeText(context,"User Information is Edited",Toast.LENGTH_SHORT).show()
                  dialog.dismiss()
              }
              .setNegativeButton("Cancel"){
                      dialog,_->
                  dialog.dismiss()

              }
              .create()
              .show()
      }
  }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val v  = inflater.inflate(R.layout.list_account_item,parent,false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        if(position == selectedItemPosition) {
            holder.selectedBg()

        } else {
            holder.defaultBg()
        }

        val newList = personSignalProtocolAsJsonList[position]
        holder.name.text = newList.name
       /* holder.store.text = newList.preKeys
        holder.preKey.text = newList.signedPreKey*/
        holder.address.text = newList.address

        /*holder.itemView.setOnClickListener {
            onClickListener.onClick(position)

        }*/
    }

    override fun getItemCount(): Int {
      return  personSignalProtocolAsJsonList.size
    }



    /*class OnClickListener(val clickListener: (personSignalProtocolAsJsonData: PersonSignalProtocolAsJsonData) -> Unit) {
        fun onClick(personSignalProtocolAsJsonData: PersonSignalProtocolAsJsonData) = clickListener(personSignalProtocolAsJsonData)
    }*/
}