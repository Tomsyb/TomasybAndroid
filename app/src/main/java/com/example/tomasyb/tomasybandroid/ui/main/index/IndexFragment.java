package com.example.tomasyb.tomasybandroid.ui.main.index;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tomasyb.baselib.base.mvp.BaseFragment;
import com.example.tomasyb.baselib.refresh.SmartRefreshLayout;
import com.example.tomasyb.baselib.refresh.api.RefreshHeader;
import com.example.tomasyb.baselib.refresh.api.RefreshLayout;
import com.example.tomasyb.baselib.refresh.listener.SimpleMultiPurposeListener;
import com.example.tomasyb.baselib.util.ActivityUtils;
import com.example.tomasyb.baselib.util.ScreenUtils;
import com.example.tomasyb.baselib.util.SizeUtils;
import com.example.tomasyb.baselib.util.ToastUtils;
import com.example.tomasyb.baselib.widget.viewpager.ComFragmentAdapter;
import com.example.tomasyb.tomasybandroid.R;
import com.example.tomasyb.tomasybandroid.ui.main.CircleFriendsActivity;
import com.example.tomasyb.tomasybandroid.ui.main.contact.IndexContact;
import com.example.tomasyb.tomasybandroid.ui.main.index.holder.LocalImageHolderView;
import com.example.tomasyb.tomasybandroid.ui.main.presenter.IndexPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.agora.yview.banner.ConvenientBanner;
import io.agora.yview.banner.holder.CBViewHolderCreator;
import io.agora.yview.banner.listener.OnItemClickListener;
import io.agora.yview.scrollview.JudgeNestedScrollView;
import io.agora.yview.tablayout.SlidingTabLayout;
import io.agora.yview.tablayout.listener.OnTabSelectListener;


/**
 * 首页fragment
 *
 * @author 严博
 * @version 1.0.0
 * @date 2018-6-4.15:50
 * @since JDK 1.8
 */

public class IndexFragment extends BaseFragment<IndexContact.presenter> implements IndexContact
        .view, OnItemClickListener {
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.index_banner)
    ConvenientBanner mBanner;
    @BindView(R.id.slitab)
    SlidingTabLayout mSlidTabLayout;
    @BindView(R.id.slitab_title)
    SlidingTabLayout mSlidTabLayoutTitle;
    @BindView(R.id.view_pager)
    ViewPager mVp;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scrollview)
    JudgeNestedScrollView scrollView;
    @BindView(R.id.buttonBarLayout)
    ButtonBarLayout buttonBarLayout;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    private final String[] mTitles = {
            "动态", "文章", "问答"
    };
    Unbinder unbinder;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private int mOffset = 0;
    private int mScrollY = 0;
    int toolBarPositionY = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_main_index;
    }

    @Override
    public IndexContact.presenter initPresenter() {
        return new IndexPresenter(this);
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initViews();
        initRefreshLayout();
        initBanner();
    }

    @Override
    protected void initData() {

    }


    @Override
    public void showLoadingDialog(String msg) {

    }

    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void initRefreshLayout() {
        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent,
                                       int offset, int headerHeight, int maxDragHeight) {
                mOffset = offset / 2;
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                dealWithViewPager();
            }
        });
    }

    @Override
    public void initViews() {
        mVp.setAdapter(new ComFragmentAdapter(getActivity().getSupportFragmentManager(),
                getFragments()));
        mVp.setOffscreenPageLimit(10);
        mSlidTabLayout.setViewPager(mVp, mTitles);
        mSlidTabLayoutTitle.setViewPager(mVp, mTitles);
        mSlidTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mVp.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mSlidTabLayoutTitle.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mVp.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int lastScrollY = 0;
            int h = SizeUtils.dp2px(170);
            int color = ContextCompat.getColor(getActivity().getApplicationContext(), R.color
                    .y_main_white) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int
                    oldScrollX, int oldScrollY) {
                int[] location = new int[2];
                mSlidTabLayout.getLocationOnScreen(location);
                int yPosition = location[1];
                if (yPosition < toolBarPositionY) {
                    mSlidTabLayoutTitle.setVisibility(View.VISIBLE);
                    scrollView.setNeedScroll(false);
                } else {
                    mSlidTabLayoutTitle.setVisibility(View.GONE);
                    scrollView.setNeedScroll(true);
                }
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBarLayout.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    //ivHeader.setTranslationY(mOffset - mScrollY);
                }
                if (scrollY == 0) {
                    //ivBack.setImageResource(R.drawable.back_white);
                    ivMenu.setImageResource(R.mipmap.ic_menu_black);
                } else {
                    //ivBack.setImageResource(R.drawable.back_black);
                    ivMenu.setImageResource(R.mipmap.ic_menu_black);
                }
                lastScrollY = scrollY;
            }
        });
        buttonBarLayout.setAlpha(0);
        toolbar.setBackgroundColor(0);
    }


    @Override
    public void initBanner() {
        mBanner.setPages(new CBViewHolderCreator() {
            @Override
            public LocalImageHolderView createHolder(View itemView) {
                return new LocalImageHolderView(itemView);
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_banner_img;
            }
        }, mPresenter.getLocationBannerData())
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.bga_banner_point_disabled, R.drawable
                        .bga_banner_point_enabled,})
                .setOnItemClickListener(this)
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        //      .setOnPageChangeListener(this)设置翻页监听
        //        convenientBanner.setManualPageable(false);//设置不能手动影响
    }


    @Override
    public void dealWithViewPager() {
        toolBarPositionY = toolbar.getHeight();
        ViewGroup.LayoutParams params = mVp.getLayoutParams();
        params.height = ScreenUtils.getScreenHeight() - toolBarPositionY - mSlidTabLayout
                .getHeight() + 1;
        mVp.setLayoutParams(params);
    }

    @Override
    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(IndexOneFragment.getInstance());
        fragments.add(IndexOneFragment.getInstance());
        fragments.add(IndexOneFragment.getInstance());
        return fragments;
    }

    @Override
    public void onItemClick(int position) {
        ToastUtils.showLong("你点击的是" + position);
    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        mBanner.startTurning();
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        mBanner.stopTurning();
    }


    @OnClick({R.id.tv_head, R.id.tv_circle_friends})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_head:
                break;
            case R.id.tv_circle_friends:
                ActivityUtils.startActivity(CircleFriendsActivity.class);
                break;
        }
    }
}
