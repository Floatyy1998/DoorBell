package com.konraddinges.smarteklingel;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;




import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Taster extends AppCompatActivity {

    private EditText taster_edt, taster_name,tuer_edt;
    private Button taster_btn;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, db1, tasterListe, gateway;
    private List<String> list, list_taster,gateway_list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taster);
        FloatingActionButton fab = findViewById(R.id.fab);
        taster_btn = findViewById(R.id.taster_button);
        taster_edt = findViewById(R.id.taster_edt);
        tuer_edt = findViewById(R.id.tuer_edt);
        taster_name = findViewById(R.id.taster_edt_name);
        mAuth = FirebaseAuth.getInstance();
        final String user = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("Taster");
        db1 = FirebaseDatabase.getInstance().getReference().child("Taster");
        gateway = FirebaseDatabase.getInstance().getReference("Gateway");
        list = new ArrayList<>();
        list_taster = new ArrayList<>();
        gateway_list = new ArrayList<>();
        tasterListe = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("TasterListe");





        recyclerView = (RecyclerView) findViewById(R.id.taster_recyclerview);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        gateway.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    gateway_list.add(String.valueOf(snapshot.getKey()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        tasterListe.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list_taster.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list_taster.remove(dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(String.valueOf(snapshot.getKey()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        taster_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gateway_list.contains(String.valueOf(tuer_edt.getText()))){
                    if(list.contains(String.valueOf(taster_edt.getText()))){
                        if (!list_taster.contains(String.valueOf(taster_edt.getText()))){
                            gateway.child(String.valueOf(tuer_edt.getText())).child("Taster").child(String.valueOf(taster_edt.getText())).child("User")
                                    .child(user).setValue(user);
                            String key = databaseReference.push().getKey();
                            databaseReference.child(key).child("ID").setValue(String.valueOf(taster_edt.getText()));
                            databaseReference.child(key).child("Name").setValue(String.valueOf(taster_name.getText()));
                            databaseReference.child(key).child("Key").setValue(key);
                            databaseReference.child(key).child("Gateway").setValue(String.valueOf(tuer_edt.getText()));
                            tasterListe.child(String.valueOf(taster_edt.getText())).setValue(String.valueOf(taster_edt.getText()));

                            taster_edt.setText("");
                            taster_name.setText("");
                            tuer_edt.setText("");

                        }
                        else {
                            taster_edt.setError("Taster schon hinzugefügt");
                            taster_edt.requestFocus();
                        }

                    }
                    else {
                        taster_edt.setError("Taster nicht vorhanden");
                        taster_edt.requestFocus();
                    }

                }
                else {
                    tuer_edt.setError("Tür nicht vorhanden");
                    tuer_edt.requestFocus();
                }
            }


        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sesam, öffne dich!", Toast.LENGTH_LONG).show();
            }
        });


        Query query = databaseReference;


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
        final FirebaseRecyclerAdapter<Item_Taster, Taster.TimeViewHolder> adapter
                = new FirebaseRecyclerAdapter<Item_Taster, Taster.TimeViewHolder>(options) {

            @NonNull
            @Override
            public Taster.TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_taster, parent, false);
                Taster.TimeViewHolder viewHolder = new Taster.TimeViewHolder(view);
                return viewHolder;
            }

            @Override
            public void onDataChanged() {


            }

            @Override
            protected void onBindViewHolder(@NonNull TimeViewHolder timeViewHolder, int i, @NonNull Item_Taster item_taster) {
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


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Taster.this);
                    builder.setCancelable(true);
                    builder.setTitle("Taster löschen?");
                    builder.setMessage("Taster "+name.getText()+" wirklich löschen?");
                    builder.setPositiveButton("Ja",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseReference.child("Gateway").child(String.valueOf(Gateway.getText())).child("Taster").child(String.valueOf(ID.getText())).removeValue();

                                    databaseReference.child("User").child(user).child("Taster").child(String.valueOf(Key.getText())).removeValue();
                                    tasterListe.child(String.valueOf(ID.getText())).removeValue();
                                }
                            });
                    builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),ID.getText(),Toast.LENGTH_LONG).show();
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
