package dals;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.models.FirebaseItem;

import java.util.ArrayList;

public class FirebaseItemDAO {
    public static final String DATABASE_NAME = "k234112eSales.db";
    public static final String TABLE_NAME = "FirebaseItem";
    private static SQLiteDatabase database = null;

    private static void openDatabase(Context context) {
        database = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "price REAL, " +
                "quantity INTEGER" +
                ")");
    }

    public static ArrayList<FirebaseItem> getItems(Context context) {
        ArrayList<FirebaseItem> items = new ArrayList<>();
        try {
            openDatabase(context);
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                int quantity = cursor.getInt(3);
                items.add(new FirebaseItem(id, name, price, quantity));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
        return items;
    }

    public static void saveItem(Context context, FirebaseItem item) {
        try {
            openDatabase(context);
            ContentValues values = new ContentValues();
            values.put("id", item.getId());
            values.put("name", item.getName());
            values.put("price", item.getPrice());
            values.put("quantity", item.getQuantity());
            database.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public static void deleteItem(Context context, String id) {
        try {
            openDatabase(context);
            database.delete(TABLE_NAME, "id = ?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    public static void clearAll(Context context) {
        try {
            openDatabase(context);
            database.delete(TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }
}
