package com.demo.mall.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.demo.demo.myapplication.R;
import com.demo.fragment.BaseActivity;
import com.demo.fragment.MainActivity;
import com.demo.fragment.TMSystemBarTintManager;
import com.demo.mall.adapter.SpecialAdapter;
import com.demo.mall.bean.FindFeatureBean;
import com.demo.my.activity.Activity_ShoppingCart;
import com.demo.utils.SpName;
import com.demo.utils.SpUtil;
import com.demo.utils.ToastUtil;
import com.demo.utils.URL;
import com.demo.view.DialogProgressbar;
import com.demo.view.myListView.XListView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：sonnerly on 2016/8/6 0006 14:53
 * 小吃
 */
public class SnackActivity extends Activity implements XListView.IXListViewListener{
    @Bind(R.id.im_snack_back)
    ImageView imSnackBack;
    @Bind(R.id.iv_snack_cart)
    ImageView ivSnackCart;
    SpecialAdapter specialAdapter;
    @Bind(R.id.lv_aty_snack)
    XListView lvAtySnack;

    BadgeView badgeView;
    public int mPage = 1;
    boolean judge_Refresh = true;
    List<FindFeatureBean.DataBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_activity_snack);
        ButterKnife.bind(this);
        //setTranslucentStatus();
        badgeView = new BadgeView(SnackActivity.this, ivSnackCart);

//        shopCar();

        mPage = 1;
        list = new ArrayList<>();
        specialAdapter = new SpecialAdapter(SnackActivity.this, list);
        lvAtySnack.setAdapter(specialAdapter);

        findSnack();

        //启用或禁用上拉加载更多功能。
        lvAtySnack.setPullLoadEnable(true);
        //启用或禁用下拉刷新功能。
        //mListView.setPullRefreshEnable(true);

        lvAtySnack.setXListViewListener(this);
    }
    /**
     * 是电池空白部分为布局颜色
     */
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        TMSystemBarTintManager tintManager = new TMSystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(0);//状态栏无背景
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (SpUtil.getString(SnackActivity.this, SpName.token, "").equals("")) {

        } else {
            shopCar();
        }
    }

    @OnClick({R.id.im_snack_back, R.id.iv_snack_cart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_snack_back:
                finish();
                break;
            case R.id.iv_snack_cart:
                Intent intent = new Intent(getApplicationContext(), Activity_ShoppingCart.class);
                startActivity(intent);
                break;
        }
    }

    private void findSnack() {
        RequestParams params = new RequestParams();
//        params.addHeader("Authorization", SpUtil.getString(getApplication(), SpName.token, ""));
        params.addQueryStringParameter("page", mPage + "");
        params.addQueryStringParameter("rows", 10 + "");
        HttpUtils http = new HttpUtils();
        http.configCurrentHttpCacheExpiry(0 * 1000);
        http.configTimeout(15 * 1000);// 连接超时  //指的是连接一个url的连接等待时间。
        http.configSoTimeout(15 * 1000);// 获取数据超时  //指的是连接上一个url，获取response的返回等待时间
        http.send(HttpRequest.HttpMethod.GET, URL.findAllSnackTwo, params,
                new RequestCallBack<String>() {
                    DialogProgressbar dialogProgressbar=new DialogProgressbar(SnackActivity.this,R.style.AlertDialogStyle);
                    @Override
                    public void onStart() {
                        super.onStart();
                        dialogProgressbar.setCancelable(false);//点击对话框以外的地方不关闭  把返回键禁止了
                        dialogProgressbar.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        dialogProgressbar.dismiss();

                        try {
                            FindFeatureBean findFeatureBean = new Gson().fromJson(responseInfo.result, FindFeatureBean.class);
                            int i = findFeatureBean.getHeader().getStatus();
                            if (i == 0) {
                                list.addAll(findFeatureBean.getData());
                                specialAdapter.notifyDataSetChanged();

                                //分页
                                onLoad();
                                if (findFeatureBean.getData().size() < 10) {
                                    judge_Refresh = false;
                                    lvAtySnack.setFooterTextView("已加载显示完全部内容");
                                }
                            } else {
                                ToastUtil.show(getApplicationContext(), findFeatureBean.getHeader().getMsg());
                            }
                        } catch (Exception e) {
                            ToastUtil.show(getApplicationContext(), "解析数据错误");
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.show(getApplicationContext(), "连接网络失败");
                    }
                });

    }

    private void shopCar() {
        //购物车脚标
        RequestParams params1 = new RequestParams();
        params1.addHeader("Authorization", SpUtil.getString(SnackActivity.this, SpName.token, ""));
        HttpUtils http1 = new HttpUtils();
        http1.configCurrentHttpCacheExpiry(0 * 1000);
        http1.send(HttpRequest.HttpMethod.GET, URL.getNumber, params1,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseInfo.result);
                            JSONObject header = jsonObject.getJSONObject("header");
                            int i = header.getInt("status");
                            if (i == 0) {

                                badgeView.setText(jsonObject.getString("data"));
                                badgeView.setTextSize(10f);
                                badgeView.setBackgroundResource(R.mipmap.dayuanchengse);
                                badgeView.setTextColor(Color.WHITE);
                                badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//                                badgeView.setAlpha(1f);
                                badgeView.setBadgeMargin(0, 0);
                                if (jsonObject.getString("data").equals("0")) {
                                    badgeView.hide();
                                } else {
                                    badgeView.show();
                                }
                            } /*else if (i == 3) {
                                //异地登录对话框，必须传.this  不能传Context
                                MainActivity.state_Three(SnackActivity.this);
                            } else {
                                ToastUtil.show(SnackActivity.this, header.getString("msg"));
                            }*/
                        } catch (Exception e) {
                            ToastUtil.show(SnackActivity.this, e.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                    }
                });
    }


    @Override
    public void onRefresh() {
        mPage = 1;
        list.clear();
        judge_Refresh = true;
        findSnack();
        //更新上方日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        lvAtySnack.setRefreshTime(str);
    }

    @Override
    public void onLoadMore() {
        //判断是否到底部  还是否需要刷新

        if (judge_Refresh) {
            mPage = mPage + 1;
            findSnack();
        } else {
            onLoad();
            lvAtySnack.setFooterTextView("已加载显示完全部内容");
        }
    }

    private void onLoad() {
        lvAtySnack.stopRefresh();
        lvAtySnack.stopLoadMore();
    }
}
