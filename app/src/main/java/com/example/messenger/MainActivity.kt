package com.example.messenger

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket



class MainActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter

    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null
    private lateinit var userName: String

    private val IP = "192.168.121.248"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //получаем имя по ключу из первого активити
        //
        userName = intent.getStringExtra("Name").toString()

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Инициализация адаптера
        adapter = MessageAdapter(mutableListOf(), userName)
        recyclerView.adapter = adapter

        // Подключаемся к серверу и начинаем слушать сообщения
        connectToServer()
        listenForMessages()

        //первое обновление то есть работа адаптера
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = RetrofitClient.instance.getMessages()
                withContext(Dispatchers.Main){
                    adapter = MessageAdapter(messages.toMutableList(),userName)
                    recyclerView.adapter = adapter
                }
            }catch (e:Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,"Ошибка загрузки",Toast.LENGTH_SHORT).show()
                }
            }
        }

        //свапает сообщение для дальнейшего удаление если условия подходит
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val position = viewHolder.adapterPosition
                val name = adapter.getName(position)
                return userName.toString().trim() == name.toString().trim()
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val name = adapter.getName(position)
                if (userName.toString().trim() == name.toString().trim()){
                    adapter.deleteMessage(position)
                    NotifyDeleteMessage(position)

                }else{
                    adapter.notifyItemChanged(position)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)




        // Обработчик кнопки отправки сообщений
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageEditText.text.clear()
            } else {
                Toast.makeText(this, "Пустое сообщение", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //конект к серверу
    private fun connectToServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MainActivity", "Attempting to connect to server...")
                socket = Socket(IP, 12345) // Замените на IP вашего сервера
                writer = OutputStreamWriter(socket!!.getOutputStream())
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                // Отправляем имя пользователя на сервер
                sendUserName(userName)
                Log.d("MainActivity", "Connected to server")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error connecting to server", e)
            }
        }
    }


    //отправляет сообщение на сервер
    //где оно попадает в бд
    private fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                writer?.write("$message\n")
                writer?.flush()
                Log.d("MainActivity", "Message sent: $message")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error sending message", e)
            }
        }
    }

    //уведомляет сервер о том что надо удалить сообщение
    private fun NotifyDeleteMessage(position: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                writer?.write("Delete_Message_@\n")
                writer?.flush()
                Log.d("MainActivity", "Delete_Message_@$position")
            } catch (e: Exception) {
                Log.e("MainActivity", "Delete_Message_@$position", e)
            }
        }

    }


    //отправляет имя юзера на сервер
    private fun sendUserName(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                writer?.write("$name\n")
                writer?.flush()
                Log.d("MainActivity", "Username sent: $name")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error sending name", e)
            }
        }
    }


    //слушает команды от сервера
    private fun listenForMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {
                    val message = reader?.readLine()
                    if (!message.isNullOrEmpty()) {
                        Log.d("DELETE_MESSAGE","$message")
                        Log.d("MainActivity", "Message received: $message")
                        if (message == "new_message") {
                            fetchAndDisplayMessages()

                        }else if(message.toString().trim().lowercase() == "Delete_Message_@".trim().lowercase())
                            Log.d("MainActivity","Сработало удаление")
                            fetchAndDisplayMessages()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error reading message", e)
            }
        }
    }


    //связана с обновление сообщений
    private fun fetchAndDisplayMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val messages = RetrofitClient.instance.getMessages()
                withContext(Dispatchers.Main) {
                    adapter.updateMessages(messages.toMutableList())
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching messages", e)
            }
        }
    }

    //отключение
    override fun onDestroy() {
        super.onDestroy()
        try {
            socket?.close()
            Log.d("MainActivity", "Socket closed")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error closing socket", e)
        }
    }
}
