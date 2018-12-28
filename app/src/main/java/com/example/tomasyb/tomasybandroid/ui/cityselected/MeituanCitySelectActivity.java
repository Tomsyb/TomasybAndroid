package com.example.tomasyb.tomasybandroid.ui.cityselected;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomasyb.baselib.yadapter.CommonAdapter;
import com.example.tomasyb.baselib.yadapter.HeaderRecyclerAndFooterWrapperAdapter;
import com.example.tomasyb.baselib.yadapter.ViewHolder;
import com.example.tomasyb.baselib.yadapter.decoration.DividerItemDecoration;
import com.example.tomasyb.tomasybandroid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.yview.indexbar.IndexBar;
import io.agora.yview.indexbar.bean.BaseIndexPinyinBean;
import io.agora.yview.indexbar.suspension.SuspensionDecoration;

/**
 * 仿美团城市选择
 */
public class MeituanCitySelectActivity extends AppCompatActivity {

    @BindView(R.id.address_book_rv)
    RecyclerView mCityRv;
    @BindView(R.id.indexbar)
    IndexBar mIndexbar;
    @BindView(R.id.tvSideBarHint)
    TextView mTvSideBarHint;
    private LinearLayoutManager mManager;
    // 设置给IndexBar,ItemDecoration 的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;
    // 美团头部数据源
    private List<MeituanHeaderBean> mHeaderDatas;
    // 主题部分数据源
    private List<MeiTuanBean> mBodyDatas;
    private CommonAdapter<MeiTuanBean> mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeadWrap;
    private SuspensionDecoration mDecoration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meituan_city_select);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mCityRv.setLayoutManager(mManager = new LinearLayoutManager(this));
        mSourceDatas = new ArrayList<>();
        mHeaderDatas = new ArrayList<>();
        //当前城市
        List<String> locationCity = new ArrayList<>();
        locationCity.add("定位中");
        mHeaderDatas.add(new MeituanHeaderBean(locationCity,"定位城市","定"));
        List<String> recentCitys = new ArrayList<>();
        mHeaderDatas.add(new MeituanHeaderBean(recentCitys,"最近访问城市","近"));
        List<String> hotCitys = new ArrayList<>();
        mHeaderDatas.add(new MeituanHeaderBean(hotCitys,"热门城市","热"));
        mSourceDatas.addAll(mHeaderDatas);

        //适配器
        mAdapter = new CommonAdapter<MeiTuanBean>(this,R.layout.item_address_book,mBodyDatas) {
            @Override
            public void convert(ViewHolder holder, MeiTuanBean meiTuanBean) {
                holder.setText(R.id.tv_name, meiTuanBean.getCity());
            }
        };
        mHeadWrap = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {
            @Override
            protected void onBindHeaderHolder(ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId){
                    case R.layout.item_only_recycleview:
                        final MeituanHeaderBean meituanHeaderBean = (MeituanHeaderBean) o;
                        //网格
                        RecyclerView recyclerView = holder.getView(R.id.recycleview);
                        recyclerView.setAdapter(
                                new CommonAdapter<String>(MeituanCitySelectActivity.this, R.layout.item_text_with_stroke_grey, meituanHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final String cityName) {
                                        holder.setText(R.id.tvname, cityName);
                                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(mContext, "cityName:" + cityName, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new GridLayoutManager(MeituanCitySelectActivity.this, 3));
                        break;
                    case R.layout.item_only_text:
                        MeituanTopHeaderBean meituanTopHeaderBean = (MeituanTopHeaderBean) o;
                        holder.setText(R.id.tvCurrent, meituanTopHeaderBean.getTxt());
                        break;
                }
            }
        };
        mHeadWrap.setHeaderView(0,R.layout.item_only_text,new MeituanTopHeaderBean("当前:成都市高新区"));
        mHeadWrap.setHeaderView(1, R.layout.item_only_recycleview, mHeaderDatas.get(0));
        mHeadWrap.setHeaderView(2, R.layout.item_only_recycleview, mHeaderDatas.get(1));
        mHeadWrap.setHeaderView(3, R.layout.item_only_recycleview, mHeaderDatas.get(2));
        mCityRv.setAdapter(mHeadWrap);
        mCityRv.addItemDecoration(mDecoration = new SuspensionDecoration(this,mSourceDatas)
                .setmTitleHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics()))
                .setColorTitleBg(0xffefefef)
                .setTitleFontSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()))
                .setColorTitleFont(getResources().getColor(android.R.color.black))
                .setHeaderViewCount(mHeadWrap.getHeaderViewCount() - mHeaderDatas.size())
        );
        mCityRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //使用indexBar
        mIndexbar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setHeaderViewCount(mHeadWrap.getHeaderViewCount() - mHeaderDatas.size());

        initDatas(getResources().getStringArray(R.array.provinces));
    }

    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(final String[] data) {
        //延迟两秒 模拟加载数据中....
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBodyDatas = new ArrayList<>();
                for (int i = 0; i < data.length; i++) {
                    MeiTuanBean cityBean = new MeiTuanBean();
                    cityBean.setCity(data[i]);//设置城市名称
                    mBodyDatas.add(cityBean);
                }
                //先排序
                mIndexbar.getDataHelper().sortSourceDatas(mBodyDatas);

                mAdapter.setDatas(mBodyDatas);
                mHeadWrap.notifyDataSetChanged();
                mSourceDatas.addAll(mBodyDatas);

                mIndexbar.setmSourceDatas(mSourceDatas)//设置数据
                        .invalidate();
                mDecoration.setmDatas(mSourceDatas);
            }
        }, 1000);

        //延迟两秒加载头部
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                MeituanHeaderBean header1 = mHeaderDatas.get(0);
                header1.getCityList().clear();
                header1.getCityList().add("上海");

                MeituanHeaderBean header2 = mHeaderDatas.get(1);
                List<String> recentCitys = new ArrayList<>();
                recentCitys.add("日本");
                recentCitys.add("北京");
                header2.setCityList(recentCitys);

                MeituanHeaderBean header3 = mHeaderDatas.get(2);
                List<String> hotCitys = new ArrayList<>();
                hotCitys.add("上海");
                hotCitys.add("北京");
                hotCitys.add("杭州");
                hotCitys.add("广州");
                header3.setCityList(hotCitys);

                mHeadWrap.notifyItemRangeChanged(1, 3);

            }
        }, 2000);

    }

    /**
     * 更新数据源
     *
     * @param view
     */
    public void updateDatas(View view) {
        for (int i = 0; i < 5; i++) {
            mBodyDatas.add(new MeiTuanBean("东京"));
            mBodyDatas.add(new MeiTuanBean("大阪"));
        }
        //先排序
        mIndexbar.getDataHelper().sortSourceDatas(mBodyDatas);
        mSourceDatas.clear();
        mSourceDatas.addAll(mHeaderDatas);
        mSourceDatas.addAll(mBodyDatas);

        mHeadWrap.notifyDataSetChanged();
        mIndexbar.invalidate();
    }
}
