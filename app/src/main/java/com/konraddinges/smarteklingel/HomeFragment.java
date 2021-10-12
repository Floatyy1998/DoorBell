package com.konraddinges.smarteklingel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {
    private View TimeView;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActionBar actionBar;

    private DatabaseReference databaseReference;
    private ImageView expanseimg;
    private CircleImageView imageview;
    private Button expanse;
    private LinearLayout root;
    private ProgressBar progressBar;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;


    public HomeFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TimeView = inflater.inflate(R.layout.fragment_home, container, false);


        final FloatingActionButton fab = TimeView.findViewById(R.id.fab);
        progressBar = TimeView.findViewById(R.id.progress_bar);


        mAuth = FirebaseAuth.getInstance();
        final String user = mAuth.getCurrentUser().getUid();





        recyclerView = (RecyclerView) TimeView.findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(getContext());



        recyclerView.setLayoutManager(mLayoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TextView tv_id = recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.key);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("Nachrichten").child((String) tv_id.getText());
                databaseReference.removeValue();
                Toast.makeText(getContext(),"Nachricht gelöscht",Toast.LENGTH_SHORT).show();



            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TuerUeberblick.class);
                startActivity(intent);
            }
        });


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


        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("Nachrichten");
        databaseReference.keepSynced(true);

        return TimeView;
    }

    @Override
    public void onStart() {
        super.onStart();
        imageview = TimeView.findViewById(R.id.image_view);
        mAuth = FirebaseAuth.getInstance();
        final String user = mAuth.getCurrentUser().getUid();



        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("User").child(user).child("Tokens");


        Query query = databaseReference.orderByChild("sort");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast

                        Log.d("jannik", token);

                        db.child(token).setValue(token);

                    }
                });

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(query, new SnapshotParser<Item>() {
                            @NonNull
                            @Override
                            public Item parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Item(String.valueOf(snapshot.child("msg").getValue()),
                                        String.valueOf(snapshot.child("time").getValue()),
                                        String.valueOf(snapshot.child("image").getValue()),
                                        String.valueOf(snapshot.child("image").getValue()),
                                        String.valueOf(snapshot.child("button_name").getValue()),
                                        String.valueOf(snapshot.child("key").getValue()));
                            }
                        })
                        .build();
        final FirebaseRecyclerAdapter<Item, TimeViewHolder> adapter
                = new FirebaseRecyclerAdapter<Item, TimeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TimeViewHolder timeViewHolder, int i, @NonNull Item item) {

                timeViewHolder.setmTime(item.getTime());
                timeViewHolder.setmName(item.getName());
                timeViewHolder.setmKey(item.getKey());
                timeViewHolder.setmButton_MSG(item.getButton_MSG());
                timeViewHolder.setmImage(item.getImage());
                timeViewHolder.setmBigImage(item.getBigimage());



            }

            @NonNull
            @Override
            public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                TimeViewHolder viewHolder = new TimeViewHolder(view);
                return viewHolder;
            }

            @Override
            public void onDataChanged() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.smoothScrollToPosition(0);




                }

            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    public class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView time, name,button_msg, key;
        CircleImageView imageView;
        ImageView bigImage;
        Button expanse;
        LinearLayout root;


        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            key = itemView.findViewById(R.id.key);
            button_msg = itemView.findViewById(R.id.button_name);
            imageView = itemView.findViewById(R.id.image_view);
            bigImage = itemView.findViewById(R.id.bigimage);


            root = itemView.findViewById(R.id.geh_weg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bigImage.getVisibility() == View.GONE) {
                        root.setVisibility(View.GONE);
                        bigImage.setVisibility(View.VISIBLE);

                    } else {
                        // TransitionManager.beginDelayedTransition(root, new AutoTransition());
                        bigImage.setVisibility(View.GONE);
                        root.setVisibility(View.VISIBLE);

                    }

                }
            });
            final Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE) ;
            bigImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Bitmap bitmap = ((BitmapDrawable)bigImage.getDrawable()).getBitmap();
                    vibe.vibrate(50);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("image/*");
                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                    startActivity(Intent.createChooser(i, "Share Image"));


                    return true;
                }
            });

            bigImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bigImage.getVisibility() == View.GONE) {
                        root.setVisibility(View.GONE);
                        bigImage.setVisibility(View.VISIBLE);

                    } else {
                        // TransitionManager.beginDelayedTransition(root, new AutoTransition());
                        bigImage.setVisibility(View.GONE);
                        root.setVisibility(View.VISIBLE);

                    }

                }
            });
        }

        Uri getLocalBitmapUri(Bitmap bmp) {
            Uri bmpUri = null;
            try {
                File file =  new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                bmpUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }


        public void setmTime(String string) {
            time.setText(string);
        }

        public void setmName(String string) {
            name.setText(string);
        }
        public void setmKey(String string){key.setText(string);}
        public String getMKey(){return (String) key.getText();}

        public void setmImage(final String string) {
            Picasso.with(itemView.getContext()).load(string).fit().centerInside().noFade().placeholder(R.drawable.loading).into(imageView);
        }

        public void setmBigImage(final String string) {
            Picasso.with(itemView.getContext()).load(string).fit().centerCrop().noFade().placeholder(R.drawable.loading).into(bigImage);
        }
        public void setmButton_MSG(String string){button_msg.setText(string);}

    }
}
