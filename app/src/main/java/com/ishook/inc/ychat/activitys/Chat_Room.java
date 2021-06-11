package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.Notification.Message_service;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.VoiceActivity;
import com.ishook.inc.ychat.adapters.Chat_adapter;
import com.ishook.inc.ychat.list.Chat_list;
import com.bumptech.glide.Glide;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Chat_Room extends AppCompatActivity {



    ImageView Profilepic ,call;
    TextView UserName;
    RecyclerView rvChat;
    LinearLayoutManager recyclerViewlayoutManager;
    Context cont;
    private static int FIRST_ELEMENT = 0;

    String sessionid;
    String userName;
    String profile;
    String friendid;
    String userId;
    String uid;
    String sid;

    String OldMessage=null;
    EditText Message;
    CheckBox Attachment;
    CheckBox Send;
    ImageView chat_image;


    String imgPath, fileName;
    Bitmap bm;
    MyReciver myReciver;

    private static int RESULT_LOAD_IMG = 1;
    String encodedString;
    String imgextention;


    private Timer timer;
    private TextCrawler textCrawler;
    private ViewGroup dropPreview, dropPost;
    private ProgressBar previewAreaTitle ;
    String Htmlcode;
    private View new_msg;
    TextView new_message;
    LinearLayout layout;
    String Chat_Convo;
    Timer t = new Timer();
    ImageView deleteConvo;

    private Handler handler =new Handler();
    String ThreadId;


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;




    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable arg0) {
            // user typed: start the timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // do your actual work here
                    Chat_Room.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Message.getText().toString().contains("https://")){
                                System.out.println("Contains");
                                textCrawler.makePreview(new callback(), Message.getText().toString());
                                System.out.println("Console"+Htmlcode);
                            }
                            else{
                                System.out.println("Not Contains url");
                                Htmlcode="";
                            }
                        }
                    });

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // hide keyboard as well?
                    // InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // in.hideSoftInputFromWindow(searchText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 600); // 600ms delay before the timer executes the "run" method from TimerTask
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // nothing to do here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // user is typing: reset already started timer (if existing)


            if(s.toString().trim().length()==0){
               Send.setVisibility(View.GONE);
            } else {
                Send.setVisibility(View.VISIBLE);
            }

            if (timer != null) {
                timer.cancel();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.chat_room);

        Profilepic= (ImageView) findViewById(R.id.chat_profile);
        call= (ImageView) findViewById(R.id.call);
        UserName= (TextView) findViewById(R.id.chat_UserName);

        Message= (EditText) findViewById(R.id.typemessage);
        Attachment= (CheckBox) findViewById(R.id.attachment);
        Send= (CheckBox) findViewById(R.id.send_message);
        chat_image= (ImageView) findViewById(R.id.chat_image);
        ImageView back= (ImageView) findViewById(R.id.back_parent);
        deleteConvo= (ImageView) findViewById(R.id.delConvo);



        deleteConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //display option menu
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), deleteConvo);
                popupMenu.getMenuInflater().inflate(R.menu.deleteconvo,popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.item_deleteCon){
                            AlertDialog.Builder alertbox = new AlertDialog.Builder(Chat_Room.this);
                            alertbox.setMessage("Are you sure you want to clear messages in this chat?");
                            //  alertbox.setTitle("");
                            alertbox.setIcon(R.drawable.ic_delete_black_24dp);

                            alertbox.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0, int arg1) {
                                            new DeletConvo().execute(sid,uid,ThreadId);
                                        }
                                    });
                            alertbox.setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                            alertbox.show();
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });




        /*  RecyclerView Code  */





        rvChat= (RecyclerView) findViewById(R.id.rvChat);
        recyclerViewlayoutManager = new LinearLayoutManager(getApplicationContext());
        cont = getApplicationContext();



        Message.addTextChangedListener(searchTextWatcher);

        previewAreaTitle = (ProgressBar) findViewById(R.id.chat_preview_area);

        /** Where the previews will be dropped */
        dropPreview = (ViewGroup) findViewById(R.id.chat_drop_preview);
        System.out.println("dropview"+dropPreview.getChildCount());

        /** Where the previews will be dropped */

        textCrawler = new TextCrawler();
        final View content = getLayoutInflater().inflate(
                R.layout.preview_content, layout);
        content.setVisibility(View.GONE);

        Send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String messageData=Message.getText().toString();
                Message.setText("");

                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();


                editor.putString(Constants.KEY_fid, friendid);
                editor.putString(Constants.KEY_md, messageData);
                editor.putString(Constants.KEY_htc, Htmlcode);
                editor.apply();
                dropPreview.setVisibility(View.GONE);
                new NewMessage().execute(sid,uid,friendid,messageData,Htmlcode);
                new UserConversation().execute(sid,uid,friendid,getIntent().getStringExtra("fname"));
/*

                Intent intent = new Intent(getApplicationContext(), Message_service.class);
                intent.putExtra("sid",sid);
                intent.putExtra("uid",uid);
                intent.putExtra("fid",friendid);
                intent.putExtra("md",messageData);
                intent.putExtra("lp",Htmlcode);
                startService(intent);*/


            }

        });

        Bundle postdata = getIntent().getExtras();
        if (postdata == null) {
            return;
        }

        sessionid= getIntent().getStringExtra("sessionId");
        userId=getIntent().getStringExtra("UserId");
        userName=getIntent().getStringExtra("friendName");
        profile=getIntent().getStringExtra("profilepic");
        friendid=getIntent().getStringExtra("FriendId");




        UserName.setText(userName);
        Glide.with(getApplicationContext())
                .load(profile)
                .into(Profilepic);

        Attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadImage();
            }
        });

         SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
          sid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
          uid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
          Chat_Convo=sharedPreferences.getString(Constants.KEY_chat, "N/A");
        OldMessage=sharedPreferences.getString(Constants.KEY_OldMessage_Count,"N/A");
    //    LoadChatConer();

        chat_view();

        //new Conversation().execute(sid,uid,friendid);
        new UserConversation().execute(sid,uid,friendid,getIntent().getStringExtra("fname"));



    }

    private void Alert() {


    }


    private void chat_view() {


        
    }

    private void LoadImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }



    private class MyReciver extends ResultReceiver{
        public MyReciver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == 18 && resultData != null){

                final String result=resultData.getString("Location");

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        UpdateChat(result);

                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent lintent = new Intent(getApplicationContext(), Message_service.class);
        stopService(lintent);
    }

    private void UpdateChat(String result) {
        String s1 = null;

        List<Chat_list> data=new ArrayList<>();

        try {

            JSONObject object = new JSONObject(result);
            //  Message_Count = object.getString("messages_count");
           /*    */

            JSONArray listOfriend = object.getJSONArray("all_messages");

            if (listOfriend.length() > 0) {
                s1= listOfriend.getJSONObject(FIRST_ELEMENT).toString();// parse the date instead of toString()



                JSONObject object1=new JSONObject(s1);
                String id=object1.getString("id");
                int mid= Integer.parseInt(id);

                int fid= Integer.parseInt(friendid);

                Intent intent = new Intent(Chat_Room.this, Message_service.class);
                intent.putExtra("sleepTime", fid);
                intent.putExtra("mid", mid);
                intent.putExtra("myReciver",myReciver);
                startService(intent);

            }



            for (int i = listOfriend.length() - 1; i >= 0; i--) {
                JSONObject json_data = listOfriend.getJSONObject(i);
                //     JSONObject jsonObject=listOfriend.getJSONObject(9);


                Chat_list chat_list = new Chat_list();

                chat_list.chat_body = json_data.getString("body");
                Log.d("body", chat_list.chat_body);
                chat_list.chat_link_preview = json_data.getString("link_preview");
                chat_list.sender_id = json_data.getString("sender_id");
                Log.d("senderid", chat_list.sender_id);

                ThreadId=json_data.getString("thread_id");

                Log.d("ThreadId",ThreadId);

                chat_list.reciver_id = uid;

                Log.d("recid", chat_list.reciver_id);
                chat_list.user_name = json_data.getString("user_name");
                chat_list.link_preview = json_data.getString("link_preview");
                chat_list.id=json_data.getString("id");
                chat_list.sid=sid;
                chat_list.uid=uid;

                Log.d("pre", chat_list.link_preview);

                org.jsoup.nodes.Document doc = Jsoup.parse(chat_list.link_preview);

                Elements element1 = doc.getElementsByClass("img_link");
                Elements element2 = doc.getElementsByClass("description");
                Elements element3 = doc.getElementsByClass("url");
                Elements element4 = doc.select("div.title");
                String title = element4.text();
                String image = element1.attr("src");
                String descrpt = element2.text();
                String url = element3.text();


                chat_list.thumbimg = image;
                Log.d("preeeimage", chat_list.thumbimg);
                chat_list.thumbdesc = descrpt;
                Log.d("preeedescrpt", chat_list.thumbdesc);
                chat_list.thumbUrl = url;
                Log.d("preeeurl", chat_list.thumbUrl);
                chat_list.thumbtitle = title;
                Log.d("preeetitle", chat_list.thumbtitle);


                data.add(chat_list);
                         /*   if (!Message_Count.equals(OldMessage)){
                           // Chat(data);

                         *//*   SharedPreferences sharedPreferencess = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferencess.edit();
                            editor.putString(Constants.KEY_OldMessage_Count, Message_Count);
                            editor.putString(Constants.KEY_chat, String.valueOf(listOfriend));
                            editor.apply();*//*

                        }  */
            }

            rvChat.setAdapter(new Chat_adapter(cont, data));
            recyclerViewlayoutManager.setStackFromEnd(true);
            rvChat.setLayoutManager(recyclerViewlayoutManager);
            rvChat.getAdapter().notifyDataSetChanged();

        } catch(JSONException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                // Move to first row

                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);

                // Set the Image in ImageView
                chat_image.setVisibility(View.VISIBLE);
                chat_image.setImageURI(Uri.parse(imgPath));
                Message.setHint("Write Caption");
                Log.d("imagepath", imgPath);
                imgextention = imgPath.substring(imgPath.lastIndexOf(".") + 1);
                Log.d("imgextention", imgextention);
                bm = BitmapFactory.decodeFile(imgPath);
                encodedString = getEncoded64ImageStringFromBitmap(bm);
                Log.d("encodedString", encodedString);


                // Get the Image's file name

                String fileNameSegments[] = imgPath.split("/");

                fileName = fileNameSegments[fileNameSegments.length - 1];
                Log.d("filename", fileName);

                // Put file name in Async Http Post Param which will used in Php web app
                // Convert image to String using Base64
               // new AsyncProfile().execute(userid, sessionid, encodedString, imgextention);



            }

        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();

        }

    }
    private String getEncoded64ImageStringFromBitmap(Bitmap camimage) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        camimage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;

    }

    private class callback implements LinkPreviewCallback {
        /**
         * This view is used to be updated or added in the layout after getting
         * the result
         */
        private View mainView;
        private LinearLayout linearLayout;
        private View loading;
        private ImageView imageView;
        private String currentTitle, currentUrl, currentCannonicalUrl,
                currentDescription;
        private Bitmap[] currentImageSet;
        private Bitmap currentImage;
        private int currentItem = 0;
        private int countBigImages = 0;
        private boolean noThumb;

        @Override
        public void onPre() {
            // hideSoftKeyboard();

            currentImageSet = null;
            currentItem = 0;

            previewAreaTitle.setVisibility(View.VISIBLE);

            currentImage = null;
            noThumb = false;
            currentTitle = currentDescription = currentUrl = currentCannonicalUrl = "";

            //submitButton.setEnabled(false);


            /** Inflating the preview layout */
            mainView = getLayoutInflater().inflate(R.layout.main_view, null);

            linearLayout = (LinearLayout) mainView.findViewById(R.id.external);

            /**
             * Inflating a loading layout into Main View LinearLayout
             */
            if(dropPreview.getChildCount()==0){
                dropPreview.addView(mainView);
                System.out.println("dropview after"+dropPreview.getChildCount());}else{
                previewAreaTitle.setVisibility(View.GONE);
                Message.setFocusable(true);

            }

        }

        @Override
        public void onPos(final SourceContent sourceContent, boolean isNull) {

            /** Removing the loading layout */
            linearLayout.removeAllViews();




            previewAreaTitle.setVisibility(View.GONE);

            String img=String.valueOf(sourceContent.getImages());
            img = img.replaceAll("\\[", "").replaceAll("\\]","");
            Htmlcode=" <div class=\"liveurl liveurlwire\" style=\"display: block;\">\n" +
                    " <a style=\"\" href=" +"\""+sourceContent.getUrl()+"\""+" target=\"_blank\" class=\"previewmainurl\">\n" +
                    " <div class=\"inner\"> <div class=\"image\"><img class=\"img_link\" src="+"\""+img+"\""+">\n" +
                    " </div> <div class=\"details\"> <div class=\"info\"> <div class=\"title\">"+sourceContent.getTitle()+"</div> <div class=\"description\">"+sourceContent.getDescription()+"</div> <div class=\"url\"> "+sourceContent.getUrl()+"</div> </div> <div class=\"video\"></div> </div> </div> </a> </div>";

            Log.d("img", img);
            Log.d("url",sourceContent.getUrl());
            Log.d("url",sourceContent.getDescription());
            Log.d("url",sourceContent.getTitle());
            Log.d("Htmlcode",Htmlcode);



            currentImageSet = new Bitmap[sourceContent.getImages().size()];

            /**
             * Inflating the content layout into Main View LinearLayout
             */
            final View content = getLayoutInflater().inflate(
                    R.layout.preview_content, linearLayout);




            if (sourceContent.getTitle().equals("") && sourceContent.getDescription().equals("")){
                content.setVisibility(View.GONE);
                releasePreviewArea();
            }

            /** Fullfilling the content layout */
            final LinearLayout infoWrap = (LinearLayout) content
                    .findViewById(R.id.info_wrap);
            final LinearLayout titleWrap = (LinearLayout) infoWrap
                    .findViewById(R.id.title_wrap);

            final ImageView imageSet = (ImageView) content
                    .findViewById(R.id.image_post_set);

            final TextView close = (TextView) titleWrap
                    .findViewById(R.id.close);
            final TextView titleTextView = (TextView) titleWrap
                    .findViewById(R.id.title);

            final TextView urlTextView = (TextView) content
                    .findViewById(R.id.url);
            final TextView descriptionTextView = (TextView) content
                    .findViewById(R.id.description);





            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    releasePreviewArea();
                }
            });



            if (sourceContent.getImages().size() > 0) {



                UrlImageViewHelper.setUrlDrawable(imageSet, sourceContent
                        .getImages().get(0), new UrlImageViewCallback() {

                    @Override
                    public void onLoaded(ImageView imageView,
                                         Bitmap loadedBitmap, String url,
                                         boolean loadedFromCache) {
                        if (loadedBitmap != null) {
                            currentImage = loadedBitmap;
                            currentImageSet[0] = loadedBitmap;
                        }
                    }
                });

            } else {
                showHideImage(imageSet, infoWrap, false);
            }



            titleTextView.setText(sourceContent.getTitle());
            urlTextView.setText(sourceContent.getCannonicalUrl());
            descriptionTextView.setText(sourceContent.getDescription());



            currentTitle = sourceContent.getTitle();
            currentDescription = sourceContent.getDescription();
            currentUrl = sourceContent.getUrl();
            currentCannonicalUrl = sourceContent.getCannonicalUrl();
        }



        /**
         * Hide keyboard
         */
     /*   private void hideSoftKeyboard() {
            hideSoftKeyboard(etWireText);


        }*/

        /**
         * Show or hide the image layout according to the "No Thumbnail" ckeckbox
         */
        private void showHideImage(View image, View parent, boolean show) {
            if (show) {
                image.setVisibility(View.VISIBLE);
                parent.setPadding(5, 5, 5, 5);
                parent.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            } else {
                image.setVisibility(View.GONE);
                parent.setPadding(5, 5, 5, 5);
                parent.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 3f));
            }
        }

/*
        private void hideSoftKeyboard(EditText editText) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager
                    .hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }*/

        /**
         * Just a set of urls
         */



        private void releasePreviewArea() {
            previewAreaTitle.setVisibility(View.GONE);
            dropPreview.removeAllViews();
        }
    }


    public class UserConversation extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;



        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"messages/index/conversation_andr");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sessionId", params[0])
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("friend_id", params[2])
                        .appendQueryParameter("friend_username", params[3])
                        ;

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String s1 = null;



            List<Chat_list> data=new ArrayList<>();

            try {

                final JSONObject object = new JSONObject(s);
              //  Message_Count = object.getString("messages_count");
           /*    */

                    JSONArray listOfriend = object.getJSONArray("all_messages");

                final String finalCallerid = object.getString("caller_id");
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), VoiceActivity.class);
                        intent.putExtra("FriendId",getIntent().getStringExtra("FriendId"));
                        intent.putExtra("freindname", finalCallerid) ;
                        startActivity(intent);
                    }
                });
                /*Log.d("callerid", finalCallerid);*/



                if (listOfriend.length() > 0) {
                    s1= listOfriend.getJSONObject(FIRST_ELEMENT).toString();// parse the date instead of toString()

                    myReciver=new MyReciver(null);
                    JSONObject object1=new JSONObject(s1);
                    String id=object1.getString("id");
                    int mid= Integer.parseInt(id);
                    int fid= Integer.parseInt(friendid);

                    Intent intent = new Intent(Chat_Room.this, Message_service.class);
                    intent.putExtra("sleepTime", fid);
                    intent.putExtra("sleepTime", fid);
                    intent.putExtra("mid", mid);
                    intent.putExtra("myReciver",myReciver);
                    startService(intent);


                }



                        for (int i = listOfriend.length() - 1; i >= 0; i--) {
                            JSONObject json_data = listOfriend.getJSONObject(i);
                            //     JSONObject jsonObject=listOfriend.getJSONObject(9);


                            Chat_list chat_list = new Chat_list();

                            chat_list.chat_body = json_data.getString("body");
                            Log.d("body", chat_list.chat_body);
                            chat_list.chat_link_preview = json_data.getString("link_preview");
                            chat_list.sender_id = json_data.getString("sender_id");
                            Log.d("senderid", chat_list.sender_id);

                            ThreadId=json_data.getString("thread_id");

                            Log.d("ThreadId",ThreadId);

                            chat_list.reciver_id = uid;

                            Log.d("recid", chat_list.reciver_id);
                            chat_list.user_name = json_data.getString("user_name");
                            chat_list.link_preview = json_data.getString("link_preview");
                            chat_list.id=json_data.getString("id");
                            chat_list.sid=sid;
                            chat_list.uid=uid;

                            Log.d("pre", chat_list.link_preview);

                            org.jsoup.nodes.Document doc = Jsoup.parse(chat_list.link_preview);

                            Elements element1 = doc.getElementsByClass("img_link");
                            Elements element2 = doc.getElementsByClass("description");
                            Elements element3 = doc.getElementsByClass("url");
                            Elements element4 = doc.select("div.title");
                            String title = element4.text();
                            String image = element1.attr("src");
                            String descrpt = element2.text();
                            String url = element3.text();


                            chat_list.thumbimg = image;
                            Log.d("preeeimage", chat_list.thumbimg);
                            chat_list.thumbdesc = descrpt;
                            Log.d("preeedescrpt", chat_list.thumbdesc);
                            chat_list.thumbUrl = url;
                            Log.d("preeeurl", chat_list.thumbUrl);
                            chat_list.thumbtitle = title;
                            Log.d("preeetitle", chat_list.thumbtitle);


                            data.add(chat_list);
                         /*   if (!Message_Count.equals(OldMessage)){
                           // Chat(data);

                         *//*   SharedPreferences sharedPreferencess = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferencess.edit();
                            editor.putString(Constants.KEY_OldMessage_Count, Message_Count);
                            editor.putString(Constants.KEY_chat, String.valueOf(listOfriend));
                            editor.apply();*//*

                        }  */
                    }

                    rvChat.setAdapter(new Chat_adapter(cont, data));
                    recyclerViewlayoutManager.setStackFromEnd(true);
                    rvChat.setLayoutManager(recyclerViewlayoutManager);
                    rvChat.getAdapter().notifyDataSetChanged();

                } catch(JSONException e){
                    e.printStackTrace();
                }

        }
    }

    public void Chat(List<Chat_list> data) {
        rvChat.setAdapter(new Chat_adapter(cont, data));
        recyclerViewlayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(recyclerViewlayoutManager);
    }

    private class NewMessage  extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            previewAreaTitle.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"messages/index/send_new_message_json");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sessionId", params[0])
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("friendId", params[2])
                        .appendQueryParameter("newMessage",params[3])
                        .appendQueryParameter("link_preview",params[4])
                        ;
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    Log.d("Resulttt",result.toString());
                    return(result.toString());


                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            previewAreaTitle.setVisibility(View.GONE);

            try {
                JSONObject object=new JSONObject(s);

                if (object.getString("errorStatus").equals("true")){
                    Toast.makeText(getApplicationContext(),object.getString("errorMessage"),Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private class DeletConvo extends AsyncTask<String,String,String> {

        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"messages/index/delete_conversation_json");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sessionId", params[0])
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("threadId", params[2]);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());


                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Boolean errorStatus;
            String errorMessage;
            new UserConversation().execute(sid,uid,friendid,getIntent().getStringExtra("fname"));


        }
    }
}
