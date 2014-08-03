package com.bxbxbai.zhuanlan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import com.bxbxbai.zhuanlan.App;
import com.bxbxbai.zhuanlan.R;
import com.bxbxbai.zhuanlan.model.DailyNews;
import com.bxbxbai.zhuanlan.support.Constants;
import com.bxbxbai.zhuanlan.task.MyAsyncTask;
import com.bxbxbai.zhuanlan.task.AccelerateGetNewsTask;
import com.bxbxbai.zhuanlan.task.BaseGetNewsTask;
import com.bxbxbai.zhuanlan.task.OriginalGetNewsTask;
import com.bxbxbai.zhuanlan.ui.widget.SwipeRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.List;

public class NewsListFragment extends BaseNewsFragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseGetNewsTask.UpdateUIListener {
    private String date;

    private boolean isAutoRefresh;
    private boolean isToday;

    // Fragment is single in PortalActivity
    private boolean isSingle;
    private boolean isRefreshed = false;

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            date = bundle.getString("date");
            isToday = bundle.getBoolean("first_page?");
            isSingle = bundle.getBoolean("single?");

            setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        assert view != null;
        mListView = (ListView) view.findViewById(R.id.news_list);
        mListView.setAdapter(listAdapter);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, this));

        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemLongClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        new RecoverNewsListTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearActionMode();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isAutoRefresh = pref.getBoolean("auto_refresh?", true);

        refresh((isToday || isSingle) && isAutoRefresh && !isRefreshed);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        refresh(isVisibleToUser && isAutoRefresh && !isRefreshed);
    }

    @Override
    public void onDestroy() {
        Crouton.cancelAllCroutons();

        super.onDestroy();
    }

    @Override
    protected boolean isCleanListChoice() {
        int position = mListView.getCheckedItemPosition();
        return mListView.getFirstVisiblePosition() > position || mListView.getLastVisiblePosition() < position;
    }

    @Override
    protected void clearListChoice() {
        mListView.clearChoices();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void checkItemAtPosition(int position) {
        mListView.setItemChecked(position, true);
    }

    private void refresh(boolean prerequisite) {
        if (prerequisite) {
            doRefresh();
        }
    }

    private void doRefresh() {
        if (isToday) {
            new OriginalGetNewsTask(date, this).execute();
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (sharedPreferences.getBoolean("using_accelerate_server?", false)) {
                String serverCode;
                if (sharedPreferences.getString("which_accelerate_server", Constants.ServerCode.SAE)
                        .equals(Constants.ServerCode.SAE)) {
                    serverCode = Constants.ServerCode.SAE;
                } else {
                    serverCode = Constants.ServerCode.HEROKU;
                }
                new AccelerateGetNewsTask(serverCode, date, this).execute();
            } else {
                new OriginalGetNewsTask(date, this).execute();
            }
        }
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    @Override
    public void beforeTaskStart() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void afterTaskFinished(List<DailyNews> resultList, boolean isRefreshSuccess, boolean isContentSame) {
        clearActionMode();
        mSwipeRefreshLayout.setRefreshing(false);
        isRefreshed = true;

        if (isRefreshSuccess) {
            if (!isContentSame) {
                newsList = resultList;
                listAdapter.updateNewsList(newsList);
            }
        } else if (isAdded()) {
            Crouton.makeText(getActivity(), getActivity().getString(R.string.network_error), Style.ALERT).show();
        }
    }

    private class RecoverNewsListTask extends MyAsyncTask<Void, Void, List<DailyNews>> {

        @Override
        protected List<DailyNews> doInBackground(Void... params) {
            return App.getInstance().getDataSource().newsOfTheDay(date);
        }

        @Override
        protected void onPostExecute(List<DailyNews> newsListRecovered) {
            if (newsListRecovered != null) {
                newsList = newsListRecovered;
                listAdapter.updateNewsList(newsListRecovered);
            }
        }
    }
}