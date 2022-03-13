package com.twilio.chat.demo.activities

import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twilio.chat.Channel
import com.twilio.chat.ErrorInfo
import com.twilio.chat.Member
import com.twilio.chat.StatusListener
import com.twilio.chat.demo.R
import com.twilio.chat.demo.TwilioApplication
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.member_item_layout.view.*
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MemberList : AppCompatActivity() {
    private lateinit var members: List<Member>
    val CONNECTION_TIMEOUT = 10000
    val READ_TIMEOUT = 15000
    var sessionid: String? = null
    var userid: String? = null
    var KEY_SESSION = "session"
    var KEY_USERID = "userid"
    var PREF_FILE_NAME = ".my_pref_file"
    lateinit var progressDialog : ProgressDialog
    private var channel: Channel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)
        progressDialog = ProgressDialog(this)
        channel = intent.getParcelableExtra("channel")
        val users = TwilioApplication.instance.basicClient.chatClient!!.users
         members = channel!!.members.membersList

        rvroomMember.setHasFixedSize(true)
        rvroomMember.layoutManager = LinearLayoutManager(this)
        rvroomMember.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.member_item_layout_new, parent, false)
                return MyViewHolder(view)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val myViewHolder = holder as MyViewHolder
                myViewHolder.name.text = "${members.get(position).identity.split("_")[0]}"
            }

            override fun getItemCount(): Int {
                return members.size
            }

            inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val name = itemView.identity;
            }
        }

        val sharedPreferences = getSharedPreferences(
            "com.ishook.inc.ychat$PREF_FILE_NAME",
            MODE_PRIVATE
        )
        sessionid =
            sharedPreferences.getString(KEY_SESSION, "N/A")
        userid =
            sharedPreferences.getString(KEY_USERID, "N/A")

        addmember.setOnClickListener(View.OnClickListener {
            friendlist().execute(sessionid, userid)
        })

        memberlist_back.setOnClickListener {
            finish()
        }

        exit_room.setOnClickListener{
            progressDialog.show()
             channel!!.leave(object : StatusListener(){
                 override fun onSuccess() {
                     progressDialog.dismiss()
                     toast("Successfully exited channel")
                     setResult(Activity.RESULT_OK)
                     finish()
                 }

                 override fun onError(errorInfo: ErrorInfo?) {
                     super.onError(errorInfo)
                     progressDialog.dismiss()
                     toast("Error while exiting channel")
                 }
             })
        }
        delete_room.setOnClickListener{
            progressDialog.show()
            channel!!.leave(object : StatusListener() {
                override fun onSuccess() {
                    progressDialog.dismiss()
                    toast("Successfully Deleted channel")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    progressDialog.dismiss()
                    toast("Error Deleting channel")
                }
            })
        }

    }

    override fun onResume() {
        super.onResume()
        members = channel!!.members.membersList
        rvroomMember.adapter?.notifyDataSetChanged()
    }

    inner class friendlist :
        AsyncTask<String?, String?, String>() {
        var conn: HttpURLConnection? = null
        var url: URL? = null
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Loading..")
            progressDialog.show()
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            progressDialog.dismiss()
            val checkedItems: BooleanArray
            val mUserItems = ArrayList<Int>()
            try {
                val `object` = JSONObject(s)
                val dataList = ArrayList<String>()
                val identityData = ArrayList<String>()
                val listOfriend = `object`.getJSONArray("loginUserfriendsList")
                for (i in 0 until listOfriend.length()) {
                    val json_data = listOfriend.getJSONObject(i)
                    dataList.add(json_data.getString("UserName"))
                    identityData.add(json_data.getString("UserName")+"_"+json_data.getString("userId"))
                }
                val strArrData = dataList.toTypedArray()
                checkedItems = BooleanArray(strArrData.size)
                val mBuilder = AlertDialog.Builder(this@MemberList)
                mBuilder.setTitle("Select Friends")
                mBuilder.setSingleChoiceItems(
                    strArrData, 0
                ) { dialog, which ->
                    progressDialog.show()
                    channel!!.members.addByIdentity(identityData[which], object:StatusListener(){
                        override fun onSuccess() {
                            progressDialog.dismiss()
                            members = channel!!.members.membersList
                            rvroomMember.adapter?.notifyDataSetChanged()
                            toast("Member Added")
                        }
                    })
                    dialog.dismiss()
                }


/*
                mBuilder.setMultiChoiceItems(strArrData, checkedItems,
                    OnMultiChoiceClickListener { dialogInterface, position, isChecked -> //                        if (isChecked) {
                        //                            if (!mUserItems.contains(position)) {
                        //                                mUserItems.add(position);
                        //                            }
                        //                        } else if (mUserItems.contains(position)) {
                        //                            mUserItems.remove(position);
                        //                        }
                        if (isChecked) {
                            mUserItems.add(position)
                        } else {
                            mUserItems.remove(Integer.valueOf(position))
                        }
                    })
*/
                mBuilder.setCancelable(false)
                mBuilder.setPositiveButton(
                    "Done"
                ) { dialogInterface, which ->
                    var item = ""
                    for (i in mUserItems.indices) {
                        item = item + strArrData.get(mUserItems[i])
                        if (i != mUserItems.size - 1) {
                            item = "$item, "
                        }
                    }
                   // AddMember()
/*                    val Item: String = jabber_user + "," + item
                    //  Toast.makeText(getApplicationContext(),Item,Toast.LENGTH_LONG).show();
                    AsyncAdd(cont, Group_name).execute(userid, sessionid, Group_name, Item)*/
                }
                mBuilder.setNegativeButton(
                    "Cancel"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                mBuilder.setNeutralButton(
                    "Un Select All"
                ) { dialogInterface, which ->
                    for (i in checkedItems.indices) {
                        checkedItems[i] = false
                        mUserItems.clear()
                        //  mItemSelected.setText("");
                    }
                }
                val mDialog = mBuilder.create()
                mDialog.show()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


        override fun doInBackground(vararg params: String?): String {
            try {
                url = URL("https://ishook.com/users/friends/friends_list_andr")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            try {
                conn = url!!.openConnection() as HttpURLConnection
                conn!!.readTimeout = READ_TIMEOUT
                conn!!.connectTimeout = CONNECTION_TIMEOUT
                conn!!.requestMethod = "POST"
                conn!!.doInput = true
                conn!!.doOutput = true
                val builder = Uri.Builder()
                    .appendQueryParameter("sessionId", params[0])
                    .appendQueryParameter("UserId", params[1])
                val query = builder.build().encodedQuery
                val os = conn!!.outputStream
                val writer = BufferedWriter(
                    OutputStreamWriter(os, "UTF-8")
                )
                writer.write(query)
                writer.flush()
                writer.close()
                os.close()
                conn!!.connect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return try {
                val response_code = conn!!.responseCode

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    val input = conn!!.inputStream
                    val reader = BufferedReader(InputStreamReader(input))
                    val result = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                        Log.d("result", result.toString())
                    }

                    // Pass data to onPostExecute method
                    result.toString()
                } else {
                    "unsuccessful"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                "exception"
            } finally {
                conn!!.disconnect()
            }
        }
    }

}