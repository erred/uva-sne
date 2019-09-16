package com.opengarden.firechat.matrixsdk.p007db;

import android.content.Context;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.util.ContentUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/* renamed from: com.opengarden.firechat.matrixsdk.db.MXLatestChatMessageCache */
public class MXLatestChatMessageCache {
    private static final String FILENAME = "ConsoleLatestChatMessageCache";
    private static final String LOG_TAG = "MXLatestChatMessageCache";
    final String MXLATESTMESSAGES_STORE_FOLDER = "MXLatestMessagesStore";
    private File mLatestMessagesDirectory = null;
    private File mLatestMessagesFile = null;
    private HashMap<String, String> mLatestMesssageByRoomId = null;
    private String mUserId = null;

    public MXLatestChatMessageCache(String str) {
        this.mUserId = str;
    }

    public void clearCache(Context context) {
        ContentUtils.deleteDirectory(this.mLatestMessagesDirectory);
        this.mLatestMesssageByRoomId = null;
    }

    private void openLatestMessagesDict(Context context) {
        if (this.mLatestMesssageByRoomId == null) {
            this.mLatestMesssageByRoomId = new HashMap<>();
            try {
                this.mLatestMessagesDirectory = new File(context.getApplicationContext().getFilesDir(), "MXLatestMessagesStore");
                this.mLatestMessagesDirectory = new File(this.mLatestMessagesDirectory, this.mUserId);
                File file = this.mLatestMessagesDirectory;
                StringBuilder sb = new StringBuilder();
                sb.append(FILENAME.hashCode());
                sb.append("");
                this.mLatestMessagesFile = new File(file, sb.toString());
                if (!this.mLatestMessagesDirectory.exists()) {
                    this.mLatestMessagesDirectory.mkdirs();
                    File filesDir = context.getApplicationContext().getFilesDir();
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(FILENAME.hashCode());
                    sb2.append("");
                    File file2 = new File(filesDir, sb2.toString());
                    if (file2.exists()) {
                        file2.renameTo(this.mLatestMessagesFile);
                    }
                }
                if (this.mLatestMessagesFile.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(this.mLatestMessagesFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    this.mLatestMesssageByRoomId = (HashMap) objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## openLatestMessagesDict failed ");
                sb3.append(e.getMessage());
                Log.m211e(str, sb3.toString());
            }
        }
    }

    public String getLatestText(Context context, String str) {
        if (this.mLatestMesssageByRoomId == null) {
            openLatestMessagesDict(context);
        }
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return this.mLatestMesssageByRoomId.containsKey(str) ? (String) this.mLatestMesssageByRoomId.get(str) : "";
    }

    private void saveLatestMessagesDict(Context context) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.mLatestMessagesFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.mLatestMesssageByRoomId);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## saveLatestMessagesDict() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void updateLatestMessage(Context context, String str, String str2) {
        if (this.mLatestMesssageByRoomId == null) {
            openLatestMessagesDict(context);
        }
        if (TextUtils.isEmpty(str2)) {
            this.mLatestMesssageByRoomId.remove(str);
        }
        this.mLatestMesssageByRoomId.put(str, str2);
        saveLatestMessagesDict(context);
    }
}
