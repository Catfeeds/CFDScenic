package com.demo.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.fragment.MainActivity;
import com.demo.my.activity.Activity_MyOrder;
import com.demo.my.activity.Activity_OrderDetailsFdDp;
import com.demo.my.activity.Activity_OrderDetailsFdTc;
import com.demo.demo.myapplication.R;
import com.demo.my.bean.FdFindOrderBean;
import com.demo.my.fragment.MyOrderFdFragment;
import com.demo.utils.SpName;
import com.demo.utils.SpUtil;
import com.demo.utils.ToastUtil;
import com.demo.utils.URL;
import com.demo.view.DialogMyOrderDelete;
import com.demo.view.DialogRefund;
import com.demo.view.DialogRefundNo;
import com.demo.view.DialogRefundOk;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class MyOrderFdAdapterLv extends BaseAdapter {
    Context mContext;
    List<List<FdFindOrderBean.DataBean.OrderListBean.RowsBean>> list;


    public MyOrderFdAdapterLv(Context mContext, List<List<FdFindOrderBean.DataBean.OrderListBean.RowsBean>> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CyzMode cyzMode;
        if (convertView==null){
            cyzMode=new CyzMode();
            convertView=LayoutInflater.from(mContext).inflate(R.layout.adapter_my_order_fd,null);
            cyzMode.tv_shenqing= (TextView) convertView.findViewById(R.id.tv_shenqing);
            cyzMode.tv_wancheng= (TextView) convertView.findViewById(R.id.tv_wancheng);
            cyzMode.tv_shiyong= (TextView) convertView.findViewById(R.id.tv_qushiyong);
            cyzMode.tv_zaici= (TextView) convertView.findViewById(R.id.tv_zaici);
            cyzMode.ll_delete= (LinearLayout) convertView.findViewById(R.id.ll_jiudian);
            cyzMode.iv_delete= (ImageView) convertView.findViewById(R.id.iv_jiudian);
            cyzMode.tv_shanchu= (TextView) convertView.findViewById(R.id.tv_shanchu);

            cyzMode.iv_business= (ImageView) convertView.findViewById(R.id.iv_business);
            cyzMode.tv_OrderNumber= (TextView) convertView.findViewById(R.id.tv_OrderNumber);
            cyzMode.tv_businessName= (TextView) convertView.findViewById(R.id.tv_businessName);
            cyzMode.tv_type= (TextView) convertView.findViewById(R.id.tv_type);
            cyzMode.tv_youxiaoqi= (TextView) convertView.findViewById(R.id.tv_youxiaoqi);
            cyzMode.tv_Total= (TextView) convertView.findViewById(R.id.tv_Total);
            convertView.setTag(cyzMode);
        }else {
            cyzMode= (CyzMode) convertView.getTag();
        }

        initView(cyzMode);

        ImageLoader.getInstance().displayImage(list.get(position).get(0).getDescribe_img(), cyzMode.iv_business);

        cyzMode.tv_OrderNumber.setText("订单号："+list.get(position).get(0).getOrder_code());
        cyzMode.tv_businessName.setText(list.get(position).get(0).getName());

        //cyzMode.tv_youxiaoqi.setText("有效期");
        cyzMode.tv_Total.setText("￥"+list.get(position).get(0).getReal_price());

        if (list.get(position).get(0).getGoods_type()==1){
            cyzMode.tv_type.setText(list.get(position).get(0).getName());
        }else {
            cyzMode.tv_type.setText("单品预定订单");
        }

        if (list.get(position).get(0).getOrder_state()==2){
            cyzMode.tv_shiyong.setVisibility(View.VISIBLE);
            cyzMode.tv_shenqing.setVisibility(View.VISIBLE);
        }else if (list.get(position).get(0).getOrder_state()==3){

        }else if (list.get(position).get(0).getOrder_state()==4){
            cyzMode.tv_wancheng.setVisibility(View.VISIBLE);
            cyzMode.tv_shanchu.setVisibility(View.VISIBLE);
        }else if (list.get(position).get(0).getOrder_state()==5){
            cyzMode.tv_zaici.setVisibility(View.VISIBLE);
            cyzMode.tv_shanchu.setVisibility(View.VISIBLE);
        }else if (list.get(position).get(0).getOrder_state()==6){

        }else if (list.get(position).get(0).getOrder_state()==7){

        }

        cyzMode.tv_shiyong.setOnClickListener(new SetOnClick(position));
        cyzMode.tv_shenqing.setOnClickListener(new SetOnClick(position));
        cyzMode.tv_zaici.setOnClickListener(new SetOnClick(position));
        cyzMode.tv_shanchu.setOnClickListener(new SetOnClick(position));

        return convertView;
    }

    private class SetOnClick implements View.OnClickListener{

        private int position=0;

        public SetOnClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_shenqing:
                   /* DialogRefundOk dialogRefundOk=new DialogRefundOk(mContext,R.style.AlertDialogStyle);
                    dialogRefundOk.show();
                    DialogRefundNo dialogRefundNo=new DialogRefundNo(mContext,R.style.AlertDialogStyle);
                    dialogRefundNo.show();*/
                    DialogRefund dialogRefund=new DialogRefund(mContext,R.style.AlertDialogStyle,3,position,0);
                    dialogRefund.show();
                    break;
                case R.id.tv_qushiyong:
                    //需要判断进入那个订单详情
                    Intent intent = null;
                    if (list.get(position).get(0).getGoods_type()==1){
                        intent=new Intent(mContext, Activity_OrderDetailsFdTc.class);
                    }else{
                        intent=new Intent(mContext, Activity_OrderDetailsFdDp.class);
                    }
                    intent.putExtra("position", position);
                    intent.putExtra("type", 2 + "");
                    intent.putExtra("orderId", list.get(position).get(0).getOrder_code() + "");
                    mContext.startActivity(intent);
                    break;
                case R.id.tv_zaici:

                    break;
                case R.id.tv_shanchu:
                    DialogMyOrderDelete dialogMyOrderDelete=new DialogMyOrderDelete(mContext,R.style.AlertDialogStyle,3,position,0);
                    dialogMyOrderDelete.show();
                    break;
            }
        }
    }

    //申请退款
    public void backMoney(final int groupPosition, final int childPosition){
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", SpUtil.getString(mContext, SpName.token, ""));
        params.addQueryStringParameter("type", 2 + "");
        params.addQueryStringParameter("orderState",3+"");
        params.addQueryStringParameter("orderCode",list.get(groupPosition).get(childPosition).getOrder_code()+"");
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("UTF_8");
        http.configCurrentHttpCacheExpiry(0*1000);
        http.send(HttpRequest.HttpMethod.GET, URL.backMoney, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseInfo.result);
                            JSONObject header = jsonObject.getJSONObject("header");
                            int status = header.getInt("status");
                            if (status == 0) {
                                MyOrderFdFragment myOrderFdFragment= (MyOrderFdFragment) Activity_MyOrder.fd_1;
                                myOrderFdFragment.findOrder();
                            } else if (status == 3) {
                                //异地登录对话框，必须传.this  不能传Context
                                MainActivity.state_Three(mContext);
                            } else {
                                ToastUtil.show(mContext, header.getString("msg"));
                            }
                        } catch (Exception e) {
                            ToastUtil.show(mContext, "解析数据错误");
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.show(mContext, s);
                    }
                });
    }
    //删除订单
    public void deleteMyOrder(final int groupPosition,int childPosition){
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", SpUtil.getString(mContext, SpName.token, ""));
        params.addQueryStringParameter("type", 2 + "");
        params.addQueryStringParameter("orderState", list.get(groupPosition).get(childPosition).getOrder_state() + "");
        params.addQueryStringParameter("orderCode", list.get(groupPosition).get(childPosition).getOrder_code() + "");
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("UTF_8");
        http.configCurrentHttpCacheExpiry(0 * 1000);
        http.send(HttpRequest.HttpMethod.GET, URL.deleteMyOrder, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseInfo.result);
                            JSONObject header = jsonObject.getJSONObject("header");
                            int status = header.getInt("status");
                            if (status == 0) {
                                MyOrderFdFragment myOrderFdFragment= (MyOrderFdFragment) Activity_MyOrder.fd_2;
                                myOrderFdFragment.findOrder();
                            } else if (status == 3) {
                                //异地登录对话框，必须传.this  不能传Context
                                MainActivity.state_Three(mContext);
                            } else {
                                ToastUtil.show(mContext, header.getString("msg"));
                            }
                        } catch (Exception e) {
                            ToastUtil.show(mContext, "解析数据错误");
                        }

                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        ToastUtil.show(mContext, s);
                    }
                });
    }


    private void initView(CyzMode cyzMode){
        cyzMode.tv_shiyong.setVisibility(View.GONE);
        cyzMode.tv_shenqing.setVisibility(View.GONE);
        cyzMode.tv_wancheng.setVisibility(View.GONE);
        cyzMode.tv_zaici.setVisibility(View.GONE);
        cyzMode.tv_shanchu.setVisibility(View.GONE);
    }

    private class CyzMode {
        TextView tv_shiyong;
        TextView tv_shenqing;
        TextView tv_wancheng;
        TextView tv_zaici;
        LinearLayout ll_delete;
        ImageView   iv_delete;

        ImageView iv_business;
        TextView tv_OrderNumber;
        TextView tv_businessName;
        TextView tv_type;
        TextView tv_youxiaoqi;
        TextView tv_Total;
        TextView tv_shanchu;

    }
}
