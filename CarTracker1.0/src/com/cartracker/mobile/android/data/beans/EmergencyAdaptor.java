package com.cartracker.mobile.android.data.beans;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

import java.util.List;

/**
 * Created by jw362j on 10/13/2014.
 */
public class EmergencyAdaptor extends BaseAdapter {

    private Activity mActivity;
    private List<String> phoneNumbers;
    private InterfaceGen.itemsRefreshListener refreshListener;

    public EmergencyAdaptor(Activity mActivity, List<String> phoneNumbers,InterfaceGen.itemsRefreshListener refresh) {
        this.mActivity = mActivity;
        this.phoneNumbers = phoneNumbers;
        this.refreshListener = refresh;
    }

    public void dataReload(List<String> Numbers){
       this.phoneNumbers = Numbers;
    }
    @Override
    public int getCount() {
        if (phoneNumbers == null||phoneNumbers.size()==0) {
            return 0;
        }
        return phoneNumbers.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneNumbers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = new viewHolder(mActivity);
        setData(holder,phoneNumbers.get(position),position);
        return holder.getViewRoot();
    }

    private void setData(viewHolder holder,final String phoneNumber,final int position){
        if(holder.getCellphone_number()!=null){
            holder.getCellphone_number().setText(phoneNumber);
        }
        if (holder.getSms_test_send()!=null) {
            holder.getSms_test_send().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(VariableKeeper.simStatus){
                        Dialog dialog = null;
                        AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                        builder.setTitle(mActivity.getResources().getString(R.string.dialog_tips_title))
                                .setMessage(mActivity.getResources().getString(R.string.emergency_sms_send_warning_msg))
                                .setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        dialog = null;
                                        SystemUtil.sendSms(phoneNumber,mActivity.getResources().getString(R.string.emergency_sms_test_text));
                                        SystemUtil.MyToast(mActivity.getResources().getString(R.string.emergency_sms_test_send_sucess));
                                    }
                                }).setNegativeButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog = null;
                            }
                        });
                        dialog = builder.create();
                        dialog.show();

                    }else {
                        SystemUtil.dialogJust4TipsShow(mActivity.getResources().getString(R.string.dialog_tips_title),mActivity.getResources().getString(R.string.emergency_sms_no_sim_error_msg));
                    }

                }
            });
        }
        if (holder.getSms_test_delete()!=null) {
            holder.getSms_test_delete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(refreshListener != null)refreshListener.onRefresh(position,null);
                }
            });
        }
    }
}

class viewHolder{
    private TextView cellphone_number;
    private Button sms_test_send,sms_test_delete;
    private View viewRoot;

    viewHolder(Context context) {
        viewRoot =  View.inflate(context, R.layout.emergency_sms_item,null);
        this.cellphone_number = (TextView) viewRoot.findViewById(R.id.cellphone_number);
        this.sms_test_send = (Button) viewRoot.findViewById(R.id.sms_test_send);
        this.sms_test_delete = (Button) viewRoot.findViewById(R.id.sms_test_delete);
    }

    public View getViewRoot() {
        return viewRoot;
    }

    public void setViewRoot(View viewRoot) {
        this.viewRoot = viewRoot;
    }

    public TextView getCellphone_number() {
        return cellphone_number;
    }

    public void setCellphone_number(TextView cellphone_number) {
        this.cellphone_number = cellphone_number;
    }

    public Button getSms_test_send() {
        return sms_test_send;
    }

    public void setSms_test_send(Button sms_test_send) {
        this.sms_test_send = sms_test_send;
    }

    public Button getSms_test_delete() {
        return sms_test_delete;
    }

    public void setSms_test_delete(Button sms_test_delete) {
        this.sms_test_delete = sms_test_delete;
    }
}
