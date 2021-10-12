package com.konraddinges.smarteklingel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TuerUeberblick extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private  FirebaseAuth mAuth;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        final String user = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("Taster");



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuer_ueberblick);
        Query query = databaseReference;



        recyclerView = (RecyclerView) findViewById(R.id.taster_recyclerview);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);


        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Item_Taster>()
                        .setQuery(query, new SnapshotParser<Item_Taster>() {
                            @NonNull
                            @Override
                            public Item_Taster parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Item_Taster(String.valueOf(snapshot.child("ID").getValue()),
                                        String.valueOf(snapshot.child("Name").getValue()),
                                        String.valueOf(snapshot.child("Key").getValue()),
                                        String.valueOf(snapshot.child("Gateway").getValue()));
                            }
                        })
                        .build();
        final FirebaseRecyclerAdapter<Item_Taster, TuerUeberblick.TimeViewHolder> adapter
                = new FirebaseRecyclerAdapter<Item_Taster, TuerUeberblick.TimeViewHolder>(options) {

            @NonNull
            @Override
            public TuerUeberblick.TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_taster, parent, false);
                TuerUeberblick.TimeViewHolder viewHolder = new TimeViewHolder(view);
                return viewHolder;
            }

            @Override
            public void onDataChanged() {


            }

            @Override
            protected void onBindViewHolder(@NonNull TuerUeberblick.TimeViewHolder timeViewHolder, int i, @NonNull Item_Taster item_taster) {
                timeViewHolder.setmID(item_taster.getID());
                timeViewHolder.setmName(item_taster.getName());
                timeViewHolder.setmKey(item_taster.getKey());
                timeViewHolder.setmGateway(item_taster.getGateway());
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }


    class TimeViewHolder extends RecyclerView.ViewHolder {
        DatabaseReference databaseReference;
        FirebaseAuth mAuth;

        TextView ID, name, Key, Gateway;


        public TimeViewHolder(@NonNull final View itemView) {
            super(itemView);


            mAuth = FirebaseAuth.getInstance();
            final String user = mAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            ID = itemView.findViewById(R.id.id_taster);
            name = itemView.findViewById(R.id.name_taster);
            Key = itemView.findViewById(R.id.key_taster);
            Gateway = itemView.findViewById(R.id.gateway_taster);


            final Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) ;
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   databaseReference.child("Gateway").child(String.valueOf(Gateway.getText())).child("door_status").setValue(1);
                   vibe.vibrate(5000);
               }
           });

        }


        public void setmID(String string) {
            ID.setText(string);
        }

        public void setmName(String string) {
            name.setText(string);
        }

        public void setmKey(String string) {
            Key.setText(string);
        }
        public void setmGateway(String string){Gateway.setText(string);}
    }


}

