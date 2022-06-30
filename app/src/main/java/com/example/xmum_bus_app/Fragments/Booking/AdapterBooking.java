package com.example.xmum_bus_app.Fragments.Booking;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterBooking extends FragmentStateAdapter {
    public AdapterBooking(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new BookingHistory();
        }
        return new BookingMenu();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
