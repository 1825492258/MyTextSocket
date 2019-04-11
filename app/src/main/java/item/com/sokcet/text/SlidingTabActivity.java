package item.com.sokcet.text;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;

import item.com.sokcet.R;

/**
 * FlycoTabLayout
 * https://github.com/H07000223/FlycoTabLayout
 */
public class SlidingTabActivity extends AppCompatActivity {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, SlidingTabActivity.class);
        activity.startActivity(intent);
    }

    private final String[] mTitles = {
            "BTC", "USDT", "Android"
    };
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private MyPagerAdapter mAdapter;

    private String[] mTitles1 = {"首页", "消息", "联系人"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab);
        for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }
        /** indicator矩形圆角 */
        SlidingTabLayout tabLayout_7 = findViewById(R.id.tl_7);
        /** indicator圆角色块 */
        // SlidingTabLayout tabLayout_9 = findViewById( R.id.tl_9);

        ViewPager vp = findViewById(R.id.vp);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        tabLayout_7.setViewPager(vp, mTitles);
        //  tabLayout_9.setViewPager(vp);

        SegmentTabLayout tabLayout = findViewById(R.id.tl_1);
        tabLayout.setTabData(mTitles1);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
