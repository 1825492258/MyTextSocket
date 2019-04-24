package item.com.sokcet.text;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import item.com.sokcet.R;

public class TextFourActivity extends AppCompatActivity {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, TextFourActivity.class);
        activity.startActivity(intent);
    }

    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBarLayout;
    private NestedScrollView nestedScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_four);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mAppBarLayout = findViewById(R.id.mAppBarLayout);
        nestedScroll = findViewById(R.id.nestedScroll);
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            strings.add("---" + i);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        MyAdapter adapter = new MyAdapter(strings);
        adapter.bindToRecyclerView(mRecyclerView);
        adapter.isFirstOnly(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                View mAppBarChildAt = mAppBarLayout.getChildAt(0);
                AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams) mAppBarChildAt.getLayoutParams();
                if (firstCompletelyVisibleItemPosition == 0) {
                    Log.i("jiejie", "--滑到顶部了");
                    //banAppBarScroll(false);
                  //  nestedScroll.setNestedScrollingEnabled(true);
                } else {
                  //  nestedScroll.setNestedScrollingEnabled(false);
                   // banAppBarScroll(false);

                }

            }
        });
    }

    /**
     *
     * @param isScroll isScroll true 允许滑动 false 禁止滑动
     */
    private void banAppBarScroll(boolean isScroll){
        View mAppBarChildAt = mAppBarLayout.getChildAt(0);
        AppBarLayout.LayoutParams  mAppBarParams = (AppBarLayout.LayoutParams)mAppBarChildAt.getLayoutParams();
        if (isScroll) {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            mAppBarChildAt.setLayoutParams(mAppBarParams);
        } else {
            mAppBarParams.setScrollFlags(0);
        }

    }
    private class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter(@Nullable List<String> data) {
            super(R.layout.item_view, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.itemTitle, item);
        }
    }
}
