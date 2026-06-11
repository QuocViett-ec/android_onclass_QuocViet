package dals;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.models.MyContact;

import java.util.ArrayList;

public class MyContactDAO {
    public static ArrayList<MyContact>getContacts(Context context)
    {
        ArrayList<MyContact> contacts=new ArrayList<>();
        Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);
                MyContact contact = new MyContact(name, phone);
                contacts.add(contact);
            }
            cursor.close();
        }
        return contacts;
    }
}
