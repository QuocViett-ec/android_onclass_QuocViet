package dals;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.models.Product;

import java.util.ArrayList;

public class ProductDAO {
    public static final String DATABASE_NAME = "k234112eSales.db";
    public static final String TABLE_NAME = "Product";
    public static SQLiteDatabase database = null;

    public static ArrayList<Product> getProducts(Context context) {
        ArrayList<Product> products = new ArrayList<>();
        try {
            database = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String productId = cursor.getString(0);
                String productName = cursor.getString(1);
                int quantity = cursor.getInt(2);
                double prices = cursor.getDouble(3);
                double coupon = cursor.getDouble(4);
                double vat = cursor.getDouble(5);
                String categoryId = cursor.getString(6);

                Product product = new Product(productId, productName, quantity, prices, coupon, vat, categoryId);
                products.add(product);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null && database.isOpen()) {
                database.close();
            }
        }

        return products;
    }
}
