package com.stkj.common.printer.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.stkj.common.printer.fragment.BluetoothConnectFragment;
import com.stkj.common.printer.fragment.WifiConnectFragment;


public class ConnectAdapter  extends FragmentStateAdapter {


    public ConnectAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new WifiConnectFragment();
        }
        return new BluetoothConnectFragment();


    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
