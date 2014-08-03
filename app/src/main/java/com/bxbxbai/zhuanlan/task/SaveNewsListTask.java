package com.bxbxbai.zhuanlan.task;

import com.bxbxbai.zhuanlan.App;
import com.bxbxbai.zhuanlan.model.DailyNews;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SaveNewsListTask extends MyAsyncTask<Void, Void, Void> {
    private String date;
    private List<DailyNews> newsList;

    public SaveNewsListTask(String date, List<DailyNews> newsList) {
        this.date = date;
        this.newsList = newsList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        saveNewsList(newsList);
        return null;
    }

    private void saveNewsList(List<DailyNews> newsList) {
        Type listType = new TypeToken<List<DailyNews>>() {

        }.getType();

        App.getInstance().getDataSource().
                insertOrUpdateNewsList(date, new GsonBuilder().create().toJson(newsList, listType));
    }
}