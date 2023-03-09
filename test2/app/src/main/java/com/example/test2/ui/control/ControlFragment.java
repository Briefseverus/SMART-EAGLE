package com.example.test2.ui.control;

import static androidx.core.app.ServiceCompat.stopForeground;
import static androidx.core.content.ContextCompat.startForegroundService;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.os.StrictMode;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import com.example.test2.MainActivity2;
import com.example.test2.NotificationService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test2.databinding.FragmentControlBinding;

public class ControlFragment extends Fragment {

    private FragmentControlBinding binding;
    private TextView statusTextView;
    private Switch motionSwitch;
    private  DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ControlViewModel controlViewModel =
                new ViewModelProvider(this).get(ControlViewModel.class);
        binding = FragmentControlBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        statusTextView = binding.textView;

        motionSwitch = binding.switch1;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        motionSwitch.setChecked(false);

        mDatabase.child("motion_sensors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current value of the database path
                Long value = dataSnapshot.getValue(Long.class);
                System.out.print(value);
                // Update TextView with new value
                if(value.toString().equals("0")){
                    statusTextView.setText("Smart Eagle Deactive");
                }else{
                    statusTextView.setText("Smart Eagle Active Now");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        motionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    try {
                        Intent intent =new Intent(getActivity(),NotificationService.class);
                        getActivity().startService(intent);
                        mDatabase.child("motion_sensors").setValue(1);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    try {
                        Intent intent =new Intent(getActivity(),NotificationService.class);
                        getActivity().stopService(intent);
                        mDatabase.child("motion_sensors").setValue(0);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


        return root;
    }
    private static void turnOffSensor() throws Exception {}
    private static void turnOnSensor() throws Exception {}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}