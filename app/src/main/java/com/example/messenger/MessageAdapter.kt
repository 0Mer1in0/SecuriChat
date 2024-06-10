package com.example.messenger

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import retrofit2.Callback
import retrofit2.Call
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Response


//адаптер который распределяетс сообщение в общий чат

class MessageAdapter(private var message: MutableList<Message>, private val username:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    companion object {
        private const val VIEW_TYPE_YOU = 0
        private const val VIEW_TYPE_OTHER = 1
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.messageTextViews)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextViews)
        val linearLayoutView: LinearLayout = itemView.findViewById(R.id.cardLayout)
    }

    class MessageViewHolderYou(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextViewYou: TextView = itemView.findViewById(R.id.nameTextViewYou)
        val contentTextViewYou: TextView = itemView.findViewById(R.id.messageTextViewsYou)
        val timeTextViewYou: TextView = itemView.findViewById(R.id.timeTextViewsYou)
        val linearLayoutViewTwo: RelativeLayout = itemView.findViewById(R.id.cardLayoutYou)
    }

    override fun getItemViewType(position: Int): Int {
        return if (message[position].name == username) VIEW_TYPE_YOU else VIEW_TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_YOU) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message_for_you, parent, false)
            MessageViewHolderYou(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            MessageViewHolder(itemView)
        }
    }
    //распределяем сообщение по карточкам

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages = message[position]
        //holder.itemViewType == VIEW_TYPE_YOU
        //username.toString().trim() == messages.name.toString().trim()

        val formatterText = TextFormatter.formatText(messages.mess)

        if (holder.itemViewType == VIEW_TYPE_YOU) {
            val viewHolderYou = holder as MessageViewHolderYou
            viewHolderYou.nameTextViewYou.text = messages.name
            viewHolderYou.contentTextViewYou.text = formatterText//messages.mess
            viewHolderYou.timeTextViewYou.text = messages.createdAt
            viewHolderYou.nameTextViewYou.setTextColor(Color.WHITE)
            //viewHolderYou.linearLayoutViewTwo.setBackgroundResource(R.drawable.linearlayoutforyoushape)
            val layoutParams = viewHolderYou.linearLayoutViewTwo.layoutParams as ViewGroup.MarginLayoutParams
            viewHolderYou.linearLayoutViewTwo.layoutParams = layoutParams
        } else {
            val viewHolder = holder as MessageViewHolder
            viewHolder.nameTextView.text = messages.name
            viewHolder.contentTextView.text = formatterText//messages.mess
            viewHolder.timeTextView.text = messages.createdAt
            viewHolder.nameTextView.setTextColor(Color.WHITE)
            viewHolder.linearLayoutView.setBackgroundResource(R.drawable.linearlayotyshape)
            val layoutParams = viewHolder.linearLayoutView.layoutParams as ViewGroup.MarginLayoutParams
            viewHolder.linearLayoutView.layoutParams = layoutParams
        }
    }
    fun deleteMessage(position: Int) {
        try {
            val messages = message[position]
            RetrofitClient.instance.deleteMessage(messages.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        message.removeAt(position)
                        notifyItemRemoved(position)
                        //Toast.makeText(applicationContext,"Ваше сообщение удаленно!",Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("DELETE_ERROR", "Failed to delete message: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("DELETE_FAILURE", "Failed to delete message", t)
                }
            })
        }catch (e:Exception){
            Log.e("DELETE_ERROR","${e}")
        }

    }


    fun updateMessages(newMessages: MutableList<Message>) {
        message.clear()
        message.addAll(newMessages)
        notifyDataSetChanged()
    }


    override fun getItemCount() = message.size

    fun getName(position: Int): String {
        return message[position].name
    }


}


