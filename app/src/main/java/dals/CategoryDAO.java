package dals;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
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
        try {
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
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }

        return categories;
    }

    public static long saveCategory(Context context, Category category)
    {
        database = context.openOrCreateDatabase(DATABASE_NAME,
                MODE_PRIVATE, null);
        ContentValues values=new ContentValues();
        values.put("CateId",category.getCateId());
        values.put("CateName",category.getCateName());
        values.put("CateDescription",category.getCateDescription());
        long result=database.insert(TABLE_NAME,null,values);
        database.close();
        return result;
    }
    public static long removeCategory(Context context, Category category){
        database = context.openOrCreateDatabase(DATABASE_NAME,
                MODE_PRIVATE, null);
        long result=database.delete(TABLE_NAME,"CateId=?",
                new String[]{category.getCateId()});
        database.close();
        return result;
    }

    public static int deleteCategory(Context context, String cateId)
    {
        database = context.openOrCreateDatabase(DATABASE_NAME,
                MODE_PRIVATE, null);
        int result = database.delete(TABLE_NAME, "CateId=?", new String[]{cateId});
        database.close();
        return result;
    }

    public static int updateCategory(Context context, Category category)
    {
        database = context.openOrCreateDatabase(DATABASE_NAME,
                MODE_PRIVATE, null);
        ContentValues values=new ContentValues();
        values.put("CateName",category.getCateName());
        values.put("CateDescription",category.getCateDescription());
        int result = database.update(TABLE_NAME, values, "CateId=?", new String[]{category.getCateId()});
        database.close();
        return result;
    }
}
