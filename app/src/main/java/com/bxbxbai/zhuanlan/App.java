package com.bxbxbai.zhuanlan;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.view.WindowManager;
import com.bxbxbai.zhuanlan.db.DailyNewsDataSource;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by baia on 14-6-4.
 */
public class App extends Application {
    private static App mContext;

    private DailyNewsDataSource dataSource;

    /** 开发测试模式 */
    private static final boolean DEVELOPER_MODE = true;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        initImageLoader(getApplicationContext());
        dataSource = new DailyNewsDataSource(getApplicationContext());
        dataSource.open();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()
        .penaltyLog()
        .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectActivityLeaks()
        .detectLeakedSqlLiteObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public DailyNewsDataSource getDataSource() {
        return dataSource;
    }

    public static App getInstance(){
        return mContext;
    }

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();


    public WindowManager.LayoutParams getWindowManagerParams() {
        return wmParams;
    }

}
