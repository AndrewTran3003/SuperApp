package com.example.andrewtran.superapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrewtran.superapp.models.Conversation;
import com.example.andrewtran.superapp.models.Talk;
import com.example.andrewtran.superapp.models.Talks;
import com.example.andrewtran.superapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.TAG;

public class Message_Activity_AddConversation extends AppCompatActivity {

    private DatabaseReference conversationData;
    private DatabaseReference userData;
    private ArrayList<Talks> listOfTalks;
    private ArrayList<User> listOfUsers;
    private RecyclerView rvUserList;
    private RecyclerView.LayoutManager rvLayoutManager;
    private CustomRowAdapter rvAdapter;
    private String key;
    public Message_Activity_AddConversation() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conversation);
        rvUserList = findViewById(R.id.listOfUsers);
        rvLayoutManager = new LinearLayoutManager(this);
        rvUserList.setLayoutManager(rvLayoutManager);
        conversationData = FirebaseDatabase.getInstance("https://composed-hangar-219019-c4dcc.firebaseio.com/").getReference();
        userData = FirebaseDatabase.getInstance("https://composed-hangar-219019-9f967.firebaseio.com/").getReference();
        initUI();

    }
    private void initUI(){
        conversationData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                initAllConversation(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                initUserList(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void initAllConversation(DataSnapshot dataSnapshot){
        listOfTalks = new ArrayList<>();
        for (DataSnapshot conversationDetail: dataSnapshot.getChildren())
        {
            Log.d(TAG, "initAllConversation: "+conversationDetail);
            for (DataSnapshot conversationDetail2: conversationDetail.getChildren()){
                String talkID = conversationDetail2.getKey();
                Talks talks = new Talks();
                talks.setTalkID(talkID);
                for (DataSnapshot conversationDetail3: conversationDetail2.child("Overview").getChildren()){
                    ArrayList<String> result =(ArrayList<String>) conversationDetail3.getValue();
                    talks.addPeople(result);
                }
                listOfTalks.add(talks);
            }
        }
    }

    private void initUserList(DataSnapshot dataSnapshot){
        listOfUsers = new ArrayList<>();
        DataSnapshot userData2 = dataSnapshot.child("Users");
       for(DataSnapshot userData3 : userData2.getChildren()){
           User user = userData3.getValue(User.class);
           if(user.getEmail().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
               continue;
           }
           listOfUsers.add(user);

       }
        initListOfUser();
    }

    private void initListOfUser(){
        rvAdapter = new CustomRowAdapter(listOfUsers);
        rvUserList.setAdapter(rvAdapter);
    }

    class CustomRowAdapter extends RecyclerView.Adapter<Message_Activity_AddConversation.CustomRowAdapter.ViewHolder>{
        private ArrayList<User> UserArrayList;

        public CustomRowAdapter(ArrayList<User> UserList) {
            this.UserArrayList = UserList;
        }

        @NonNull
        @Override
        public Message_Activity_AddConversation.CustomRowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View v = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_user,viewGroup,false);
            Message_Activity_AddConversation.CustomRowAdapter.ViewHolder vh = new Message_Activity_AddConversation.CustomRowAdapter.ViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = rvUserList.getChildAdapterPosition(v);
                    User user = UserArrayList.get(position);
                    String user1 = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    String user2 = user.getEmail();
                    boolean test = checkIfTalkExist(user1,user2);
                    DatabaseReference newTalk = conversationData.child("Conversation");

                    if(test){
                        startConversation(key);
                    }
                    else{
                        addNewConversation( user1,  user2,  newTalk);
                    }
                    dataBaseListener();
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull Message_Activity_AddConversation.CustomRowAdapter.ViewHolder viewHolder, int i) {
            User user = UserArrayList.get(i);
            viewHolder.name.setText(user.getName());
            viewHolder.email.setText(user.getEmail());

        }


        @Override
        public int getItemCount() {
            return UserArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView email;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                email = itemView.findViewById(R.id.email);
            }
        }
    }

    private boolean checkIfTalkExist(String user1, String user2) {
        int count = 0;
        for (Talks talks : listOfTalks){
            if (talks.getPeople().contains(user1) && talks.getPeople().contains(user2)){
                count++;
                key = talks.getTalkID();
            }
        }
        if(count == 0){
            return false;
        }
        else{
            return true;
        }
    }
    private void addNewConversation(String user1, String user2, DatabaseReference newTalk){
        key = newTalk.push().getKey();
        DatabaseReference newTalk2 = newTalk.child(key);

        DatabaseReference Log = newTalk2.child("Log");
        DatabaseReference Overview = newTalk2.child("Overview");
        String logKey = Log.push().getKey();
        DatabaseReference Log1 = Log.child(logKey);
        HashMap<String,Object> messageMap = new HashMap<>();
        messageMap.put("msg"," ");
        messageMap.put("usr", user1);
        Log1.updateChildren(messageMap);
        DatabaseReference Overview1 = Overview.child("Users");
        HashMap<String,Object> usersMap = new HashMap<>();
        usersMap.put("0",user1);
        usersMap.put("1", user2);
        Overview1.updateChildren(usersMap);
        startConversation(key);
    }

    private void startConversation(String conversationKey) {
        Intent intent = new Intent(this,Message_Activity_ChatRoom.class);
        intent.putExtra("selected_conversation",conversationKey);
        startActivityForResult(intent,1);
    }

    private void dataBaseListener(){
        conversationData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
