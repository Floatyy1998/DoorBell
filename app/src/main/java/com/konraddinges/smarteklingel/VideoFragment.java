package com.konraddinges.smarteklingel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class VideoFragment extends Fragment {
    private WebView webView;
    private View TimeView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TimeView = inflater.inflate(R.layout.fragment_video, container,false);
        mAuth = FirebaseAuth.getInstance();
        final String user = mAuth.getCurrentUser().getUid();


        ImageButton menu = TimeView.findViewById(R.id.menu_oeffnen);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.taster:
                                Intent intent = new Intent(getContext(), Taster.class);
                                startActivity(intent);
                                return false;

                            case R.id.ausloggen:
                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if (!task.isSuccessful()) {
                                            return;
                                        }
                                        String token = task.getResult().getToken();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("Tokens").child(token);
                                        databaseReference.removeValue();
                                    }


                                });

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), Login.class));
                                return false;

                        }

                        return false;

                    }
                });// to implement on click event on items of menu
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.top_menu, popup.getMenu());
                popup.show();
            }
        });


        FloatingActionButton fab = TimeView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Sesam, öffne dich!",Toast.LENGTH_LONG).show();
            }
        });



        return TimeView;
    }
}
