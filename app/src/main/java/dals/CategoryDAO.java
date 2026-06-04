package dals;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.models.Category;

import java.util.ArrayList;

public class CategoryDAO {
    public static final String DATABASE_NAME = "k234112eSales.db";
    public static final String TABLE_NAME = "Category";
    public static SQLiteDatabase database = null;

    public static ArrayList<Category> getCategories(Context context)
    {
        ArrayList<Category> categories = new ArrayList<>();
        database = context.openOrCreateDatabase(DATABASE_NAME,
                MODE_PRIVATE, null);

        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME,
                null);
        while(cursor.moveToNext()){
            String cateId = cursor.getString(0);
            String cateName = cursor.getString(1);
            String cateDescription = cursor.getString(2);
            Category c=new Category(cateId,cateName,cateDescription);
            categories.add(c);

//To do something ….
        }
        cursor.close();


        return categories;
    }

}
