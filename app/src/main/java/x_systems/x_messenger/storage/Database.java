package x_systems.x_messenger.storage;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.application.Values;
import x_systems.x_messenger.fragments.ChatFragment;
import x_systems.x_messenger.messages.MessageController;
import x_systems.x_messenger.threading.AwaitMethod;
import x_systems.x_messenger.threading.MethodWrapper;

/**
 * Created by Manasseh on 10/8/2016.
 */

public class Database extends SQLiteOpenHelper {
    private Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "chatMessages";

    private static final String TABLE_PROPERTIES = "properties";
    private static final String KEY_PROPERTIES_ID = "_id";
    private static final String KEY_PROPERTIES_VALUE = "value";

    private static final String TABLE_CONTACTS = "contacts";
    private static final String KEY_CONTACTS_ID = "_id";
    private static final String KEY_CONTACTS_JID = "jid";
    private static final String KEY_CONTACTS_USERNAME = "username";
    private static final String KEY_CONTACTS_STATUS = "status";
    private static final String KEY_CONTACTS_ENCRYPTION_TYPE = "encryption_type";
    private static final String KEY_CONTACTS_IMAGEID = "imageid";

    private static final String TABLE_MESSAGES = "messages";
    private static final String KEY_MESSAGES_JID = "jid";
    private static final String KEY_MESSAGES_ID = "_id";
    private static final String KEY_MESSAGES_USERNAME = "username";
    private static final String KEY_MESSAGES_MESSAGE = "message";
    private static final String KEY_MESSAGES_TIME = "time";
    private static final String KEY_MESSAGES_IS_ENCRYPTED = "is_encrypted";

    private static final String TABLE_FILES = "files";
    private static final String KEY_FILES_ID = "_id";
    private static final String KEY_FILES_TITLE = "title";
    private static final String KEY_FILES_PATH = "path";
    private static final String KEY_FILES_TYPE = "type";

    private static final String TABLE_NOTES = "notes";
    private static final String KEY_NOTES_ID = "_id";
    private static final String KEY_NOTES_TITLE = "title";
    private static final String KEY_NOTES_PATH = "path";
    private static final String KEY_NOTES_CONTENT = "content";

    private static final String TABLE_SAVED_CONVERSATION = "saved_conversation";
    private static final String KEY_SAVED_CONVERSATION_ID = "_id";
    private static final String KEY_SAVED_CONVERSATION_TITLE = "title";
    private static final String KEY_SAVED_CONVERSATION_USERNAME = "username";
    private static final String KEY_SAVED_CONVERSATION_MESSAGE = "message";
    private static final String KEY_SAVED_CONVERSATION_TIME = "time";
    private static final String KEY_SAVED_CONVERSATION_IS_ENCRYPTED = "is_encrypted";

    private static final String[] TABLES = {
            TABLE_PROPERTIES,
            TABLE_CONTACTS,
            TABLE_MESSAGES,
            TABLE_FILES,
            TABLE_NOTES,
            TABLE_SAVED_CONVERSATION
    };

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_PROPERTIES =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + " INTEGER PRIMARY KEY, "
                        + KEY_PROPERTIES_VALUE + " TEXT);";
        final String INSERT_INTO_PROPERTIES_USERNAME_ID =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (0, '" + Values.User + "@" + Values.Domain + "');";
        final String INSERT_INTO_PROPERTIES_USERNAME =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (1, 'name');";
        final String INSERT_INTO_PROPERTIES_ENCRYPTED_PASSWORD =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (2, " +Values.Password +");";
        final String INSERT_INTO_PROPERTIES_ENCRYPTED_WHIPE_OUT_PASSWORD =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (3, '654321');";
        final String INSERT_INTO_PROPERTIES_SESSION_ID =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (4, 'RandomCharactersForSessionId');";
        final String INSERT_INTO_PROPERTIES_MESSAGE_DESTRUCTION_TIME =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (5, '123');";
        final String INSERT_INTO_PROPERTIES_AUTO_LOCK_TIME =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (6, '10');";
        final String INSERT_INTO_PROPERTIES_SALTED_HASH =
                "INSERT INTO "
                        + TABLE_PROPERTIES + "("
                        + KEY_PROPERTIES_ID + ", "
                        + KEY_PROPERTIES_VALUE + ") VALUES (7, '1234567890');";


        final String CREATE_TABLE_CONTACTS =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_CONTACTS + "("
                        + KEY_CONTACTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_CONTACTS_USERNAME + " TEXT, "
                        + KEY_CONTACTS_STATUS + " TEXT, "
                        + KEY_CONTACTS_JID + " TEXT, "
                        + KEY_CONTACTS_ENCRYPTION_TYPE + " TEXT, "
                        + KEY_CONTACTS_IMAGEID + " INTEGER);";
        final String CREATE_TABLE_MESSAGES =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_MESSAGES + "("
                        + KEY_MESSAGES_JID + " TEXT, "
                        + KEY_MESSAGES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_MESSAGES_USERNAME + " TEXT, "
                        + KEY_MESSAGES_MESSAGE + " TEXT, "
                        + KEY_MESSAGES_TIME + " STRING, "
                        + KEY_MESSAGES_IS_ENCRYPTED + " TEXT, "
                        + "FOREIGN KEY(" + KEY_MESSAGES_JID
                        + ") REFERENCES " + TABLE_CONTACTS + "(" + KEY_CONTACTS_JID + "));";
        final String CREATE_TABLE_FILES =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_FILES + "("
                        + KEY_FILES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_FILES_TITLE + " TEXT, "
                        + KEY_FILES_PATH + " TEXT, "
                        + KEY_FILES_TYPE + " INTEGER);";
        final String CREATE_TABLE_SAVED_CONVERSATION =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_SAVED_CONVERSATION + "("
                        + KEY_SAVED_CONVERSATION_TITLE + " TEXT, "
                        + KEY_SAVED_CONVERSATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_SAVED_CONVERSATION_USERNAME + " TEXT, "
                        + KEY_SAVED_CONVERSATION_MESSAGE + " TEXT, "
                        + KEY_SAVED_CONVERSATION_TIME + " TEXT, "
                        + KEY_SAVED_CONVERSATION_IS_ENCRYPTED + " TEXT);";
        final String CREATE_TABLE_NOTES =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_NOTES + "("
                        + KEY_NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + KEY_NOTES_TITLE + " TEXT, "
                        + KEY_NOTES_PATH + " TEXT, "
                        + KEY_NOTES_CONTENT + " TEXT);";
        db.execSQL(CREATE_TABLE_PROPERTIES);
        db.execSQL(INSERT_INTO_PROPERTIES_USERNAME_ID);
        db.execSQL(INSERT_INTO_PROPERTIES_USERNAME);
        db.execSQL(INSERT_INTO_PROPERTIES_ENCRYPTED_PASSWORD);
        db.execSQL(INSERT_INTO_PROPERTIES_SESSION_ID);
        db.execSQL(INSERT_INTO_PROPERTIES_MESSAGE_DESTRUCTION_TIME);
        db.execSQL(INSERT_INTO_PROPERTIES_ENCRYPTED_WHIPE_OUT_PASSWORD);
        db.execSQL(INSERT_INTO_PROPERTIES_AUTO_LOCK_TIME);
        db.execSQL(INSERT_INTO_PROPERTIES_SALTED_HASH);


        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_MESSAGES);
        db.execSQL(CREATE_TABLE_SAVED_CONVERSATION);
        db.execSQL(CREATE_TABLE_FILES);
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        for (String table : TABLES)
            db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }

    public String readProperty(Property.Type propertyType) {
        String returnValue = null;
        String selectQuery =
                "SELECT * FROM " + TABLE_PROPERTIES +
                        " WHERE " + KEY_PROPERTIES_ID+"="+propertyType.getValue();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            returnValue = cursor.getString(cursor.getColumnIndex(KEY_PROPERTIES_VALUE));
        }
        db.close();
        cursor.close();
        return returnValue;
    }

    public void saveConversation(String contactName) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
        String conversationTitle = "CHAT_"+simpleDateFormat.format(date);

        SQLiteDatabase db =  getWritableDatabase();
        String sql =
                "INSERT INTO "+TABLE_SAVED_CONVERSATION+" ( "
                + KEY_SAVED_CONVERSATION_TITLE+", "
                + KEY_SAVED_CONVERSATION_USERNAME+", "
                + KEY_SAVED_CONVERSATION_MESSAGE+", "
                + KEY_SAVED_CONVERSATION_TIME+", "
                + KEY_SAVED_CONVERSATION_IS_ENCRYPTED+") "
                + " SELECT "
                + "?, "
                + KEY_MESSAGES_USERNAME+", "
                + KEY_MESSAGES_MESSAGE+", "
                + KEY_MESSAGES_TIME+", "
                + KEY_MESSAGES_IS_ENCRYPTED
                + " FROM "+TABLE_MESSAGES
                + " WHERE "+KEY_MESSAGES_JID+"=?;";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, conversationTitle);
        statement.bindString(2, contactName);
        statement.executeInsert();

        String sqlFiles =
                "INSERT INTO "+TABLE_FILES+" ( "
                        + KEY_FILES_TITLE+", "
                        + KEY_FILES_PATH+", "
                        + KEY_FILES_TYPE+") VALUES (?,?,"+R.drawable.chat+");";
        statement = db.compileStatement(sqlFiles);
        statement.bindString(1, conversationTitle);
        statement.bindString(2, contactName);
        statement.executeInsert();
        db.close();
    }

    public void writeProperty(Property.Type propertyType, String newValue) {
        SQLiteDatabase db =  getWritableDatabase();
        String sql = "UPDATE "+TABLE_PROPERTIES
                + " SET "+KEY_PROPERTIES_VALUE+"=?"
                + " WHERE "+KEY_PROPERTIES_ID+"="+propertyType.getValue()+";";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, newValue);
        statement.executeInsert();
        db.close();
    }

    public void insertDecryptedMessage(final MessageController messageController, final String jid)
    {
        SQLiteDatabase db =  getWritableDatabase();
        String sql = "INSERT INTO "+TABLE_MESSAGES
                + "("+KEY_MESSAGES_USERNAME
                + ", "+KEY_MESSAGES_MESSAGE
                + ", "+KEY_MESSAGES_TIME
                + ", "+KEY_MESSAGES_JID
                + ", "+KEY_MESSAGES_IS_ENCRYPTED
                + ") VALUES (?,?,?,?,0)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, messageController.getUsername());
        statement.bindString(2, messageController.getMessage());
        statement.bindString(3, String.valueOf(messageController.getTime()));
        statement.bindString(4, jid);
        statement.executeInsert();
        db.close();
    }

    public void insertPgpMessage(final MessageController messageController, final String jid)
    {
        SQLiteDatabase db =  getWritableDatabase();
        String sql = "INSERT INTO "+TABLE_MESSAGES
                + "("+KEY_MESSAGES_USERNAME
                + ", "+KEY_MESSAGES_MESSAGE
                + ", "+KEY_MESSAGES_TIME
                + ", "+KEY_MESSAGES_JID
                + ", "+KEY_MESSAGES_IS_ENCRYPTED
                + ") VALUES (?,?,?,?,1)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, messageController.getUsername());
        statement.bindString(2, messageController.getMessage());
        statement.bindString(3, String.valueOf(messageController.getTime()));
        statement.bindString(4, jid);
        statement.executeInsert();
        db.close();
    }

    public void writeNote(String note) {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
        String conversationTitle = "NOTE_"+simpleDateFormat.format(date);

        long realTime = SystemClock.elapsedRealtime();
        Date date2 = new Date(realTime);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
        String path = "NOTE_"+simpleDateFormat2.format(date2);

        SQLiteDatabase db =  getWritableDatabase();
        String sql =
                "INSERT INTO "+TABLE_NOTES+" ( "
                        + KEY_NOTES_TITLE+", "
                        + KEY_NOTES_PATH+", "
                        + KEY_NOTES_CONTENT+") "
                        + " VALUES (?,?,?);";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, conversationTitle);
        statement.bindString(2, path);
        statement.bindString(3, note);
        statement.executeInsert();
        db.close();
    }

    public void deleteNote(int id) {

        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql =
                    "DELETE FROM " + TABLE_NOTES + " WHERE " + KEY_NOTES_ID + "=?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, String.valueOf(id));
            statement.executeUpdateDelete();
            db.close();
            System.out.println("Note deleted");
        } catch (Exception e){
            System.out.println("DELETE NOTES EXCEPTION: " + e.toString());
        }
    }

    public void deleteFile(int id){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql =
                    "DELETE FROM " + TABLE_FILES + " WHERE " + KEY_FILES_ID + "=?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, String.valueOf(id));
            statement.executeUpdateDelete();
            db.close();
            System.out.println("FILE deleted");
        } catch (Exception e){
            System.out.println("DELETE FILE EXCEPTION: " + e.toString());
        }
    }

    public void updateNote(int Id, String newNote) {
        SQLiteDatabase db =  getWritableDatabase();
        String sql =
                "UPDATE "+TABLE_NOTES+" SET "
                        + KEY_NOTES_CONTENT+"=? "
                        + "WHERE "+KEY_NOTES_ID+"=?";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, newNote);
        statement.bindString(2, String.valueOf(Id));
        statement.executeUpdateDelete();
    }

    public String getNote(int Id) {
        final String selectQuery =
                "SELECT * FROM " + TABLE_NOTES +
                        " WHERE " + KEY_NOTES_ID + "=?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {String.valueOf(Id)});
        if (cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(KEY_NOTES_CONTENT));
        }
        else {
            final String selectQuery2 =
                    "SELECT * FROM " + TABLE_NOTES +
                            " WHERE " + KEY_NOTES_ID + "=?;";
            Cursor cursor2 = db.rawQuery(selectQuery2, new String[] {String.valueOf(Id)});
            if (cursor2.moveToFirst())
            {
                return cursor2.getString(cursor2.getColumnIndex(KEY_NOTES_CONTENT));
            }
        }
        return "";
    }

    public FilesList getAllFiles() {
        System.out.println("showing files now");
        FilesList filesList = new FilesList();
        String selectQuery =
                "SELECT * FROM "+TABLE_FILES;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast())
        {
            do {
                if(!cursor.getString(cursor.getColumnIndex(KEY_FILES_TITLE)).substring(0, 4).equals("NOTE")) {
                    filesList.fileTitles.add(cursor.getString(cursor.getColumnIndex(KEY_FILES_TITLE)));
                    System.out.println(cursor.getString(cursor.getColumnIndex(KEY_FILES_TITLE)));
                    filesList.filePaths.add(cursor.getString(cursor.getColumnIndex(KEY_FILES_PATH)));
                    filesList.fileTypes.add(FilesList.resourceIdToType(cursor.getInt(cursor.getColumnIndex(KEY_FILES_TYPE))));
                    filesList.fileIds.add(cursor.getInt(cursor.getColumnIndex(KEY_FILES_ID)));
                    System.out.println("fileid = " + cursor.getInt(cursor.getColumnIndex(KEY_FILES_ID)));
                }
            } while (cursor.moveToPrevious());
        }

         selectQuery =
                "SELECT * FROM "+TABLE_NOTES;;
         cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast())
        {
            do {

                    filesList.fileTitles.add(cursor.getString(cursor.getColumnIndex(KEY_NOTES_TITLE)));
                    System.out.println(cursor.getString(cursor.getColumnIndex(KEY_NOTES_TITLE)));
                    filesList.filePaths.add(cursor.getString(cursor.getColumnIndex(KEY_NOTES_PATH)));
                    filesList.fileTypes.add(FilesList.resourceIdToType(R.drawable.note));
                    filesList.fileIds.add(Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_NOTES_ID))));
                    System.out.println("noteid = " + cursor.getInt(cursor.getColumnIndex(KEY_FILES_ID)));

            } while (cursor.moveToPrevious());
        }

        db.close();
        cursor.close();
        return filesList;
    }

    public String[][] readEarlierMessages(final String jid)
    {
        final String selectQuery =
                "SELECT * FROM " + TABLE_MESSAGES +
                        " WHERE " + KEY_MESSAGES_JID + "=?;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {jid});
        final int count = cursor.getCount();

        String[][] returnValue = new String[count][];
        if (cursor.moveToLast())
        {
            int i = 0;
            do {
                returnValue[i] = new String[]{
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_TIME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_IS_ENCRYPTED))
                };
                i++;
            } while (cursor.moveToPrevious());
        }

        db.close();
        cursor.close();
        return returnValue;
    }

    public String readSavedMessages(String fileName) {
        final String selectQuery =
                "SELECT * FROM " + TABLE_MESSAGES +
                        " WHERE " + KEY_MESSAGES_JID + "=?;";
        // TODO: Change to statement!
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {fileName});
        final int count = cursor.getCount();

        String[][] returnValue = new String[count][];
        if (cursor.moveToLast())
        {
            int i = 0;
            do {
                returnValue[i] = new String[]{
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_TIME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_IS_ENCRYPTED))
                };
                i++;
            } while (cursor.moveToPrevious());
        }

        String result = "";
        for (String[] stringArray : returnValue) {
            long unixTime  = Long.parseLong(stringArray[2]);
            Date date = new Date(unixTime);
            SimpleDateFormat hoursMinutes = new SimpleDateFormat("HH:mm MM/dd/yyyy");
            String time = hoursMinutes.format(date);

            String username = stringArray[0];
            String message = stringArray[1];

            result += time + " " + username + " " + message + "\n";
        }
        db.close();
        cursor.close();
        return result;
    }

    public void updateContacts(ContactList newContactList) {
        List<String> JIDs = newContactList.JIDs;
        List<String> names = newContactList.names;
        List<Integer> iconIds = newContactList.iconIds;
        List<String> status = newContactList.status;
        List<Integer> types = newContactList.encryptionTypes;

        ContactList previousContactList = getUsers();
        for (String JID : previousContactList.JIDs)
        {
            if (JIDs.contains(JID))
            {
                SQLiteDatabase db =  getWritableDatabase();
                String sql = "UPDATE "+TABLE_CONTACTS
                        + " SET "
                        + KEY_CONTACTS_IMAGEID+"="+iconIds.get(JIDs.indexOf(JID))+", "
                        + KEY_CONTACTS_STATUS+"=?, "
                        + KEY_CONTACTS_USERNAME+"=?, "
                        + KEY_CONTACTS_ENCRYPTION_TYPE+"=? "
                        + "WHERE "+KEY_CONTACTS_JID+"=?";
                SQLiteStatement statement = db.compileStatement(sql);
                statement.bindString(1, status.get(JIDs.indexOf(JID)));
                statement.bindString(2, names.get(JIDs.indexOf(JID)));
                statement.bindString(3, JID);
                statement.bindString(4, types.get(JIDs.indexOf(JID)).toString());
                statement.executeInsert();
                db.close();
                names.remove(JIDs.indexOf(JID));
                status.remove(JIDs.indexOf(JID));
                iconIds.remove(JIDs.indexOf(JID));
                JIDs.remove(JID);
                System.out.println("Updating contact: "+JID);
            }
            else
            {
                removeIfOtrContact(JID);
                System.out.println("Removing contact: "+JID);
            }
        }

        for (String JID : JIDs)
        {
            System.out.println("Adding contact " + JID);
            addContact(
                    names.get(JIDs.indexOf(JID)),
                    status.get(JIDs.indexOf(JID)),
                    JID,
                    iconIds.get(JIDs.indexOf(JID)),
                    ChatFragment.Type.OTR.getResourceId()
            );
            System.out.println("Adding contact: "+ JID);
        }
    }

    public ContactList getUsers() {
        ContactList returnValue = new ContactList();
        String selectQuery =
                "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast())
        {
            do {
                returnValue.status.add("unavailable");
                returnValue.JIDs.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_JID)));
                returnValue.iconIds.add(cursor.getInt(cursor.getColumnIndex(KEY_CONTACTS_IMAGEID)));
                returnValue.names.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_USERNAME)));
                returnValue.status.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_STATUS)));
                returnValue.encryptionTypes.add(cursor.getInt(cursor.getColumnIndex(KEY_CONTACTS_ENCRYPTION_TYPE)));
            } while (cursor.moveToPrevious());
        }
        db.close();
        cursor.close();
        return returnValue;
    }

    public void addContact(String username, String status, String jid, Integer imageId, Integer encryptionType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO "+TABLE_CONTACTS
                + "("+KEY_CONTACTS_USERNAME
                + ", "+KEY_CONTACTS_STATUS
                + ", "+KEY_CONTACTS_JID
                + ", "+KEY_CONTACTS_IMAGEID
                + ", "+KEY_CONTACTS_ENCRYPTION_TYPE
                + ") VALUES (?,?,?,"+imageId+","+encryptionType+")";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, username);
        statement.bindString(2, status);
        statement.bindString(3, jid);
        statement.executeInsert();
        db.close();
    }

    public void removeIfOtrContact(String jid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+TABLE_CONTACTS
                + " WHERE "+KEY_CONTACTS_JID+"=? AND "+KEY_CONTACTS_ENCRYPTION_TYPE+"="+R.drawable.otr;
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, jid);
        statement.executeUpdateDelete();
        db.close();
    }

    private void clearContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CONTACTS);
        db.close();
    }

    public void clearMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MESSAGES);
        db.close();
    }

    public void removeAllMessagesOlderThan(long unixTime) {
        ContactList returnValue = new ContactList();
        String selectQuery =
                "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast())
        {
            do {
                returnValue.status.add("unavailable");
                returnValue.JIDs.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_JID)));
                returnValue.iconIds.add(cursor.getInt(cursor.getColumnIndex(KEY_CONTACTS_IMAGEID)));
                returnValue.names.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_USERNAME)));
                returnValue.status.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_STATUS)));
              //  returnValue.encryptionTypes.add(cursor.getInt(cursor.getColumnIndex(KEY_CONTACTS_ENCRYPTION_TYPE)));
            } while (cursor.moveToPrevious());
        }
        db.close();
        cursor.close();
    }

    public void clearNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTES);
        db.close();
    }

    public void clearFiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FILES);
        db.close();
        clearNotes();
        //clearMessages();
    }

    public void clearMessages(String jid) {
        SQLiteDatabase db =  getWritableDatabase();
        String sql = "DELETE FROM "+TABLE_MESSAGES
                + " WHERE "+KEY_MESSAGES_JID+"=?;";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindString(1, jid);
        statement.executeUpdateDelete();
        db.close();
    }

    public ChatsList getChatsList() {
        ChatsList chatsList = new ChatsList();
        String selectQuery =
                "SELECT * FROM "+TABLE_MESSAGES
                + " GROUP BY "+KEY_MESSAGES_JID;
        String selectContactQuery = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToLast())
        {
            do {
                chatsList.JIDs.add(cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_JID)));
                long time = Long.valueOf(cursor.getString(cursor.getColumnIndex(KEY_MESSAGES_TIME)));
                Date date = new Date(time);
                SimpleDateFormat daysMonthsYearsTime = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                chatsList.dateTimes.add(daysMonthsYearsTime.format(date));
            } while (cursor.moveToPrevious());
        }

        for (String jid : chatsList.JIDs)
        {
            selectContactQuery =
                    "SELECT * FROM "+TABLE_CONTACTS
                            + " WHERE "+KEY_CONTACTS_JID+"=?;";
            cursor = db.rawQuery(selectContactQuery, new String[] {jid});
            if (cursor.moveToLast())
            {
                do {
                    chatsList.photos.add(cursor.getInt(cursor.getColumnIndex(KEY_CONTACTS_IMAGEID)));
                    chatsList.encryptionTypes.add(
                            ChatFragment.resourceIdToType(
                                    cursor.getInt(cursor.getColumnIndex(KEY_CONTACTS_ENCRYPTION_TYPE))
                            )
                    );
                    chatsList.contactNames.add(cursor.getString(cursor.getColumnIndex(KEY_CONTACTS_USERNAME)));
                } while (cursor.moveToPrevious());
            }
        }
        db.close();
        cursor.close();
        return chatsList;
    }

    public void setNote(String UserID, String ChatID)
    {

    }

    public Boolean offlineLogin(String password)
    {
        return Objects.equals(password, readProperty(Property.Type.ENCRYPTED_PASSWORD));
    }

    private int getColumnIdWhere(final String table, final String compareColumn, final String idColumn, final String encrypted_value) {
        AwaitMethod awaitMethod = new AwaitMethod(new MethodWrapper<String>() {
            @Override
            public String method() {
                SQLiteDatabase db =  getReadableDatabase();
                String selectQuery = "SELECT ?, ? FROM ?";
                Cursor cursor = db.rawQuery(selectQuery, new String[] {compareColumn, idColumn, table});
                String[][] encryptedRows = new String[cursor.getCount()][];
                int index = 0;
                if (cursor.moveToLast())
                {
                    do {
                        encryptedRows[index] = new String[] {
                                cursor.getString(cursor.getColumnIndex(idColumn)),
                                cursor.getString(cursor.getColumnIndex(compareColumn))
                        };
                        index++;
                    } while (cursor.moveToPrevious());
                }

                cursor.close();
                db.close();
                for (String encryptedRow[] : encryptedRows)
                    if (Objects.equals(encryptedRow[1], encrypted_value))
                        return encryptedRow[0];

                return null;
            }
        });

        return Integer.valueOf(awaitMethod.run());
    }

    /*public void changePassword(@NonNull String newHashedPassword) {
        for (String table : TABLES)
            changeTable(table, newHashedPassword);
    }

    private void changeTable(final String table, final String newHashedPassword) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT * FROM ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] {table});
        if (cursor.moveToLast())
        {
            do {
                for (String columnName : cursor.getColumnNames())
                {
                    String oldValue = cursor.getString(cursor.getColumnIndex(columnName));
                    String newValue = "";
                    try {
                        newValue = AES256.encryptText(AES256.decryptText(oldValue, hashed_password), newHashedPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String updateQuery =
                            "UPDATE TOP (1) ? SET ?=? WHERE ?=?";
                    SQLiteStatement statement = db.compileStatement(updateQuery);
                    statement.bindString(1, table);
                    statement.bindString(2, columnName);
                    statement.bindString(3, newValue);
                    statement.bindString(4, columnName);
                    statement.bindString(5, oldValue);
                    statement.executeUpdateDelete();
                }
            } while (cursor.moveToPrevious());
        }

        cursor.close();
        db.close();
    }*/

    public void wipeDatabase()
    {
        SQLiteDatabase db = getWritableDatabase();
        for (String table : TABLES)
            db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }
}
