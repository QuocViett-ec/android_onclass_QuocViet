package dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.models.Contact;

import java.util.ArrayList;

public class ContactDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "firebase_contacts.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CONTACTS = "contacts";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_EMAIL = "email";
    private static final String COL_UPDATED_AT = "updatedAt";
    private static final String COL_SYNC_STATUS = "syncStatus";

    public ContactDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_UPDATED_AT + " INTEGER, " +
                COL_SYNC_STATUS + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void insertOrUpdateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, contact.getId());
        values.put(COL_NAME, contact.getName());
        values.put(COL_PHONE, contact.getPhone());
        values.put(COL_EMAIL, contact.getEmail());
        values.put(COL_UPDATED_AT, contact.getUpdatedAt());
        values.put(COL_SYNC_STATUS, contact.getSyncStatus());
        db.insertWithOnConflict(TABLE_CONTACTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<Contact> getAllContacts() {
        return queryContacts(null, null);
    }

    public ArrayList<Contact> getVisibleContacts() {
        return queryContacts(COL_SYNC_STATUS + " != ?", new String[]{Contact.PENDING_DELETE});
    }

    public Contact getContactById(String id) {
        ArrayList<Contact> contacts = queryContacts(COL_ID + " = ?", new String[]{id});
        if (contacts.isEmpty()) {
            return null;
        }
        return contacts.get(0);
    }

    public void deleteContact(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, COL_ID + " = ?", new String[]{id});
    }

    public ArrayList<Contact> getPendingContacts() {
        return queryContacts(COL_SYNC_STATUS + " != ?", new String[]{Contact.SYNCED});
    }

    public void markSynced(String id) {
        Contact contact = getContactById(id);
        if (contact == null) {
            return;
        }
        contact.setSyncStatus(Contact.SYNCED);
        contact.setUpdatedAt(System.currentTimeMillis());
        insertOrUpdateContact(contact);
    }

    public void markPendingDelete(String id) {
        Contact contact = getContactById(id);
        if (contact == null) {
            return;
        }
        contact.setSyncStatus(Contact.PENDING_DELETE);
        contact.setUpdatedAt(System.currentTimeMillis());
        insertOrUpdateContact(contact);
    }

    private ArrayList<Contact> queryContacts(String selection, String[] selectionArgs) {
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, null, selection, selectionArgs, null, null, COL_ID + " ASC");
        try {
            while (cursor.moveToNext()) {
                contacts.add(new Contact(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_UPDATED_AT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_SYNC_STATUS))
                ));
            }
        } finally {
            cursor.close();
        }
        return contacts;
    }
}
