package com.joyappsdevteam.androidchatbotdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.joyappsdevteam.androidchatbotdemo.Adapter.ChatMessageAdapter;
import com.joyappsdevteam.androidchatbotdemo.Model.ChatMessage;
import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText editTextMsg;
    Button button_send;
    ImageView imageView;
    int ASK_MULTIPLE_PERMISSION = 1;

    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();



    }

    private void continueTheWork(){

        listView = findViewById(R.id.listView);
        button_send = findViewById(R.id.btmSend);
        editTextMsg = findViewById(R.id.editTextMsg);
        imageView = findViewById(R.id.imageView);

        adapter = new ChatMessageAdapter(this,new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = editTextMsg.getText().toString();
                String response = chat.multisentenceRespond(editTextMsg.getText().toString());

                if (TextUtils.isEmpty(message)){
                    Toast.makeText(MainActivity.this,"Please enter a query",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(message);
                botsReply(response);

                //clear editText
                editTextMsg.setText("");
                listView.setSelection(adapter.getCount() - 1);

            }
        });

        boolean available = isSDCartAvailable();
        if (available) Toast.makeText(this,"SDCart Available",Toast.LENGTH_SHORT).show();

        AssetManager assets = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageDirectory().toString() + "/TBC/bots/TBC");

        boolean makeFile = fileName.mkdirs();

        if (makeFile) Toast.makeText(this,"Directory created",Toast.LENGTH_SHORT).show();

        if (fileName.exists()){

            //read the line

            try {
                for (String dir : (assets.list("TBC"))){
                    File sunDir = new File(fileName.getPath() + "/" + dir);
                    boolean sunDir_Check = sunDir.mkdirs();

                    if (sunDir_Check) Toast.makeText(this,"Sub-Directory created",Toast.LENGTH_SHORT).show();

                    for (String file : (assets.list("TBC/" + dir))){
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);

                        if (newFile.exists()){
                            continue;
                        }

                        InputStream in = assets.open("TBC/" + dir + "/" + file);
                        OutputStream out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);

                        //copy files from assets to the mobile's sd card or any secondary memory available

                        copyFile(in,out);
                        in.close();
                        out.flush();
                        out.close();

                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        bot = new Bot("TBC", MagicStrings.root_path, "chat");
        chat = new Chat(bot);

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        while((read = in.read(buffer)) != -1){
            out.write(buffer,0,read);
        }
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(false,true,message);
        adapter.add(chatMessage);

    }

    private void botsReply(String response) {
        ChatMessage chatMessage = new ChatMessage(false,false,response);
        adapter.add(chatMessage);
    }

    public static boolean isSDCartAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true : false;
    }

    private void askPermissions() {
        int hasWritePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasRightPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasMountPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED ||
                hasRightPermission != PackageManager.PERMISSION_GRANTED ||
                hasMountPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                   ASK_MULTIPLE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    continueTheWork();
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                //Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                finish();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
