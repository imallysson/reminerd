package com.magnificus.reminerd.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.magnificus.reminerd.Entities.CategoryEntity;
import com.magnificus.reminerd.Entities.ColorEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allysson on 31/05/17.
 */

public class CategoryRepository extends SQLiteOpenHelper {

    private final Context context;

    public CategoryRepository(Context context) {
        super(context, "Categories", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Categories (" +
                "ID INTEGER PRIMARY KEY," +
                "Name TEXT NOT NULL, " +
                "IDColorEntity INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*String sql = "";
        switch (oldVersion) {
            case 1:
                sql = "QUERY AQUI";
                db.execSQL(sql);
        }*/
    }

    public List<CategoryEntity> getCategories() {
        String sql = "SELECT * FROM Categories";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<CategoryEntity> categories = populateCategories(c);

        return categories;
    }

    @NonNull
    public List<CategoryEntity> populateCategories(Cursor c) {
        List<CategoryEntity> categories = new ArrayList<CategoryEntity>();

        while (c.moveToNext()) {
            CategoryEntity category = new CategoryEntity();
            category.setID(c.getLong(c.getColumnIndex("ID")));
            category.setName(c.getString(c.getColumnIndex("Name")));
            category.setIDColorEntity(c.getLong(c.getColumnIndex("IDColorEntity")));
            category.setColorEntity(this.setColorObject(category.getIDColorEntity()));

            categories.add(category);
        }

        return categories;
    }

    public CategoryEntity getCategory(Long id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Categories WHERE ID = ?";
        Cursor c = db.rawQuery(sql, new String[]{id.toString()});
        CategoryEntity categoryEntity = new CategoryEntity();
        if (c != null) {
            if(c.moveToFirst()) {
                categoryEntity.setID(c.getLong(c.getColumnIndex("ID")));
                categoryEntity.setName(c.getString(c.getColumnIndex("Name")));
                categoryEntity.setIDColorEntity(c.getLong(c.getColumnIndex("IDColorEntity")));
                categoryEntity.setColorEntity(this.setColorObject(categoryEntity.getIDColorEntity()));

                return categoryEntity;
            }
        }
        return null;
    }

    //TODO: This gambex needs to be resolved later
    public ColorEntity setColorObject(Long id) {
        ColorRepository colorRepository = new ColorRepository(this.context);
        ColorEntity colorEntity = colorRepository.getColor(id);

        if (colorEntity != null) {
            return colorEntity;
        } else {
            colorEntity.setID((long) 666);
            colorEntity.setName("Gray");
            colorEntity.setHexadecimal("#CCC");

            return colorEntity;
        }
    }

    public void insert(CategoryEntity categoryEntity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = buildCategoryObject(categoryEntity);
        db.insert("Categories", null, data);
    }

    public void update(CategoryEntity categoryEntity) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues data = buildCategoryObject(categoryEntity);

        String[] params = {categoryEntity.getID().toString()};
        db.update("Categories", data, "ID = ?", params);
    }

    public void delete(CategoryEntity categoryEntity) {
        SQLiteDatabase db = getWritableDatabase();

        String[] params = {categoryEntity.getID().toString()};
        db.delete("Categories", "id = ?", params);
    }

    @NonNull
    private ContentValues buildCategoryObject(CategoryEntity categoryEntity) {
        ContentValues data = new ContentValues();
        data.put("ID", categoryEntity.getID());
        data.put("Name", categoryEntity.getName());
        data.put("IDColorEntity", categoryEntity.getIDColorEntity());

        return data;
    }
}