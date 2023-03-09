package com.example.test2.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.*;

import com.example.test2.GlideApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test2.databinding.FragmentHomeBinding;

import java.util.Arrays;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private DatabaseReference mDatabase;

    String[] times =new String[1000];


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        String filePath = null;
        ImageView[] imageview={
                binding.imageView,
                binding.imageView2,
                binding.imageView3,
                binding.imageView4,
                binding.imageView5
        };
        TextView[] textview={
                binding.textView,
                binding.textView2,
                binding.textView3,
                binding.textView4,
                binding.textView5
        };

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    int j=0;
                    HashMap<String, Object> childData = (HashMap<String, Object>) childSnapshot.getValue();
                    String timestamp = (String) childData.get("time");
                    times[j]=timestamp;
                    j++;
                    Log.d("Firebase", "" + timestamp);
                    if(j>999)return;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        try {
            for(int k=0, h= times.length-1;k<h;k++,h--){
                String temp=times[k];
                times[k]=times[h];
                times[h]=temp;}
                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference imageRef = storage.getReferenceFromUrl("gs://smarteagle-a3f43.appspot.com/home/pi/Pictures");

                for(int i=0;i<5;i++){
                    filePath=times[i]+".jpg";
                    StorageReference dispRef=imageRef.child(filePath);
                    GlideApp.with(this).load(dispRef).into(imageview[i]);
                    StringBuilder sb =new StringBuilder();
                    sb.append(times[i]);
                    sb.delete(18,25);
                    textview[i].setText(sb.toString());
                }
        }catch (Exception e){
            for(int k=0, h= times.length-1;k<h;k++,h--){
                String temp=times[k];
                times[k]=times[h];
                times[h]=temp;}
            for(int i=0;i>5;i++){
            StringBuilder sb =new StringBuilder();
            sb.append(times[i]);
            sb.delete(18,25);
            textview[i].setText(sb.toString());}
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}