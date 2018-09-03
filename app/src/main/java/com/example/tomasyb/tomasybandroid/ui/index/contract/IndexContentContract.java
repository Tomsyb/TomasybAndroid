package com.example.tomasyb.tomasybandroid.ui.index.contract;

import com.example.tomasyb.baselib.base.BasePresenter;
import com.example.tomasyb.baselib.base.IBaseModel;
import com.example.tomasyb.baselib.base.IBaseView;
import com.example.tomasyb.tomasybandroid.bean.NewsSummary;

import java.util.List;



/**
 * 首页内容Contract
 *
 * @author 严博
 * @version 1.0.0
 * @date 2018-6-7.15:05
 * @since JDK 1.8
 */

public interface IndexContentContract {
    interface Model extends IBaseModel{

    }
    interface View extends IBaseView{
        void changeData(List<NewsSummary> mData);

    }
    abstract class Presenter extends BasePresenter<View,Model> {
        public abstract void getIndexList(String type,String id,int startPage);
    }
}
