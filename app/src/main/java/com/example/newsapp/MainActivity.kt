package com.example.newsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.sql.Types.NULL
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        @JvmField
        var search: String? = null

    }
    var news = ArrayList<String>()
    var listView: ListView? = null
    var adapter: ArrayAdapter<String>? = null
    var editText: EditText? = null
    var button: Button? = null
    var textView: TextView?=null
    var webActivityIntent: Intent? = null
    var newsapi: String? = null
    var date: String? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        editText = findViewById(R.id.editText)
        textView=findViewById(R.id.textView)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        button = findViewById(R.id.button)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, news)
        listView?.setAdapter(adapter)
        val incomingintent = getIntent()

        webActivityIntent = Intent(applicationContext, WebActivity::class.java)
        val locale = this.resources.configuration.locale.country
        Log.i("country", locale.toLowerCase())

        if (CalenderActivity.check == 1) {

            textView?.setText(search)
            date = incomingintent.getStringExtra("date")
            news.clear()
            adapter!!.notifyDataSetChanged()

            if (search!!.isNotEmpty()) {
                date = CalenderActivity.date
                newsapi = "https://newsapi.org/v2/everything?language=en&q=query&from="+date.toString()+"&to="+date.toString()+"&apiKey=3b2b0944f5e0436bb4c4cc8e64dcb85a"
                newsapi = newsapi!!.replace("q=query", "q=" + search)

                Log.i("date1", date)
                Log.i("num", newsapi)
                val jd = jsondata()
                jd.execute(newsapi)
            } else {
                Toast.makeText(this, "Keyword cannot be empty, showing Top Stories of the day", Toast.LENGTH_LONG).show()
                defaultNews()
            }
        } else {
            defaultNews()
        }
    }

    // Function to show default news
    fun defaultNews(){
        textView?.setText(R.string.top_news)
        // Default value is US but will be modified according to country
        val defaultVal: String = "https://newsapi.org/v2/top-headlines?country=us&apiKey=3b2b0944f5e0436bb4c4cc8e64dcb85a"
        val jd = jsondata()
        jd.execute(defaultVal)
    }

    // Get date from calendar activity
    fun calendar(view: View?) {
        search = editText!!.text.toString()
        val intent1 = Intent(applicationContext, CalenderActivity::class.java)
        startActivity(intent1)
        Log.i("check", CalenderActivity.check.toString());
    }

    // Automatic search
    fun search(view: View?) {
        news.clear()
        adapter!!.notifyDataSetChanged()
        val search = editText!!.text
        if (search.length > 0) {
            date = CalenderActivity.date
            Log.i("date1", date)
            newsapi = "https://newsapi.org/v2/everything?language=en&q=query&from="+date.toString()+"&to="+date.toString()+"&apiKey=3b2b0944f5e0436bb4c4cc8e64dcb85a"
            newsapi = newsapi!!.replace("q=query", "q="+search)
            Log.i("num", newsapi)
            val jd = jsondata()
            jd.execute(newsapi)
        } else {
            Toast.makeText(this, "please enter a valid keyword", Toast.LENGTH_LONG).show()
        }
    }

    // JSON data async task to pull data
    inner class jsondata : AsyncTask<String?, Void?, String?>() {
        override fun doInBackground(vararg p0: String?): String? {
            var url: URL? = null
            try {
                Log.i("query string",p0[0])
                url = URL(p0[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                val r = BufferedReader(InputStreamReader(`in`))
                val total = r.use(BufferedReader::readText)
                //Log.i("json", total)
                return total
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        // update list after async execute
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            var outerObject: JSONObject? = null
            try {
                outerObject = JSONObject(s)
                val jsonArray = outerObject.getJSONArray("articles")
                Log.i("data", jsonArray.length().toString() + "")
                var i = 0
                val size = jsonArray.length()
                while (i < size) {
                    val objectInArray = jsonArray.getJSONObject(i)
                    news.add(objectInArray["title"] as String)
                    adapter!!.notifyDataSetChanged()
                    listView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
                        try {
                            val jsonObject1 = jsonArray.getJSONObject(position)
                            val urlinfo1 = jsonObject1.getString("url")
                            webActivityIntent!!.putExtra("url", urlinfo1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        startActivity(webActivityIntent)
                    }
                    i++
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            adapter!!.notifyDataSetChanged()
        }
    }
}