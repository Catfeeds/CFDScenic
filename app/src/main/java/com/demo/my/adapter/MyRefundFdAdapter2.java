package com.demo.my.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.demo.myapplication.R;
import com.demo.fragment.MainActivity;
import com.demo.my.activity.Activity_RefundDetails_fd;
import com.demo.my.activity.Activity_RefundOrder;
import com.demo.my.bean.RefundOrderFdBean;
import com.demo.my.fragment.MyRefundFdFragment;
import com.demo.utils.SpName;
import com.demo.utils.SpUtil;
import com.demo.utils.ToastUtil;
import com.demo.utils.URL;
import com.demo.view.DialogMyOrderDelete;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class MyRefundFdAdapter2 extends BaseAdapter {
    Context mContext;
    List<List<RefundOrderFdBean.DataBean.OrderListBean.RowsBean>> list;


    public MyRefundFdAdapter2(Context mContext, List<List<RefundOrderFdBean.DataBean.OrderListBean.RowsBean>> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        CyzMode cyzMode;
        if (convertView==null){
            cyzMode=new CyzMode();
            convertView=LayoutInflater.from(mContext).inflate(R.layout.adapter_refund_order_fd2,null);
            cyzMode.lv_listView= (ListView) convertView.findViewById(R.id.lv_listView);
            cyzMode.tv_OrderNumber= (TextView) convertView.findViewById(R.id.tv_OrderNumber);

            cyzMode.tv_yituikuan= (TextView) convertView.findViewById(R.id.tv_yituikuan);
            cyzMode.tv_tuikuanzhong= (TextView) convertView.findViewById(R.id.tv_tuikuanzhong);
            cyzMode.tv_shanchu= (TextView) convertView.findViewById(R.id.tv_shanchu);
            convertView.setTag(cyzMode);
        }else {
            cyzMode= (CyzMode) convertView.getTag();
        }

        initView(cyzMode);

        cyzMode.lv_listView.setAdapter(new MyRefundFdAdapterN(mContext,list.get(position)));

        cyzMode.tv_OrderNumber.setText("订单号：" + list.get(position).get(0).getOrder_code());

        if (list.get(position).get(0).getOrder_state()==3){
            cyzMode.tv_tuikuanzhong.setVisibility(View.VISIBLE);
        }else if (list.get(position).get(0).getOrder_state()==6){
            cyzMode.tv_yituikuan.setVisibility(View.VISIBLE);
            cyzMode.tv_shanchu.setVisibility(View.VISIBLE);
        }

        cyzMode.tv_shanchu.setOnClickListener(new SetOnClick(position));

        cyzMode.lv_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int cposition, long id) {
                Intent intent = new Intent(mContext, Activity_RefundDetails_fd.class);
                intent.putExtra("type", 2 + "");
                intent.putExtra("orderId", list.get(position).get(0).getOrder_code() + "");
                mContext.startActivity(intent);
            }
        });
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
                case R.id.tv_shanchu:
                    DialogMyOrderDelete dialogMyOrderDelete=new DialogMyOrderDelete(mContext,R.style.AlertDialogStyle,7,position,0);
                    dialogMyOrderDelete.show();
                    break;
            }
        }
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
                                MyRefundFdFragment myRefundFdFragment= (MyRefundFdFragment) Activity_RefundOrder.fd_1;
                                myRefundFdFragment.mPage=1;
                                myRefundFdFragment.list.clear();
                                myRefundFdFragment.refundOrder();
                                ToastUtil.show(mContext, "删除成功");
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
        cyzMode.tv_yituikuan.setVisibility(View.GONE);
        cyzMode.tv_tuikuanzhong.setVisibility(View.GONE);
        cyzMode.tv_shanchu.setVisibility(View.GONE);
    }

    private class CyzMode {
        ListView lv_listView;

        TextView tv_OrderNumber;

        TextView tv_tuikuanzhong;
        TextView tv_yituikuan;
        TextView tv_shanchu;
    }
}
