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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.andrewtran.superapp.models.Conversation;
import com.example.andrewtran.superapp.models.Talk;
import com.example.andrewtran.superapp.models.Talks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Message_Activity_ChatRoom extends AppCompatActivity {

    private static final String TAG = "Blah";
    private Button sendButton;
    private EditText textMessage;
    private RecyclerView rvChatLog;
    private List<Talk> listConversation;
    private ArrayAdapter arrayAdapter;
    private String username, selectedConversation,user_msg_key;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;

    private CustomRowAdapter customRowAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        rvChatLog = findViewById(R.id.chatLog);
        layoutManager = new LinearLayoutManager(this);

        rvChatLog.setLayoutManager(layoutManager);
        sendButton = findViewById(R.id.sendButton);
        textMessage = findViewById(R.id.messageChat);
        username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        selectedConversation = getIntent().getExtras().get("selected_conversation").toString();

        databaseReference= FirebaseDatabase.getInstance("https://composed-hangar-219019-c4dcc.firebaseio.com").getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new loadConversation(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        refreshData();


            sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!textMessage.getText().toString().isEmpty()){
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    DatabaseReference databaseReference1 = databaseReference.child("Conversation").child(selectedConversation).child("Log");
                    user_msg_key = databaseReference1.push().getKey();
                    databaseReference1.updateChildren(map);
                    DatabaseReference databaseReference2 = databaseReference1.child(user_msg_key);
                    HashMap<String,Object> map2 = new HashMap<String, Object>();
                    map2.put("msg",textMessage.getText().toString());
                    map2.put("usr", username);
                    databaseReference2.updateChildren(map2);
                    initChatlog();
                    textMessage.setText("");
                    refreshData();
                }

            }
        });

    }

    public void initConversation(DataSnapshot dataSnapshot){
        listConversation = new ArrayList<Talk>();
        for (DataSnapshot conversationDetail: dataSnapshot.getChildren())
        {
            DataSnapshot conversationDetail2 = conversationDetail.child(selectedConversation);
            Log.d(TAG, "onDataChange: " +conversationDetail2);
            DataSnapshot conversationDetail3 = conversationDetail2.child("Log");
            Log.d(TAG, "onDataChange: " +conversationDetail3);
            for (DataSnapshot conversationDetail4:conversationDetail3.getChildren()){
                Talk talk = conversationDetail4.getValue(Talk.class);
                listConversation.add(talk);
            }

        }
    }



    public void initChatlog(){
      customRowAdapter = new CustomRowAdapter(listConversation);
      rvChatLog.setAdapter(customRowAdapter);
      rvChatLog.scrollToPosition(listConversation.size()-1);
    }










    class CustomRowAdapter extends RecyclerView.Adapter{
        private List<Talk> conversationArrayList;

        public CustomRowAdapter(List<Talk> conversationArrayList) {
            this.conversationArrayList = conversationArrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view;
            if (i == 0){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_bubblechat_sent,viewGroup,false);
                return new sentMessage(view);
            }
            else if (i == 1){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_bubblechat_received,viewGroup,false);
                return new receivedMessage(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Talk talk = conversationArrayList.get(i);
            if(viewHolder.getItemViewType() == 0){
                ((sentMessage)viewHolder).bind(talk);
            }
            else if (viewHolder.getItemViewType() == 1){
                ((receivedMessage)viewHolder).bind(talk);
            }
        }




        @Override
        public int getItemViewType(int position) {
            Talk talk = conversationArrayList.get(position);
            Log.d(TAG, "getItemViewType: "+ username+" " +talk.getUsr());
            if (talk.getUsr().contains(username)){
                return 0;
            }
            else{
                return 1;
            }

        }

        @Override
        public int getItemCount() {
            return conversationArrayList.size();
        }

        public class sentMessage extends RecyclerView.ViewHolder {

            public com.github.library.bubbleview.BubbleTextView text;
            public sentMessage(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.sentBubble);
            }
            public void bind(Talk talk){
                text.setText(talk.getMsg());
            }
        }
        public class receivedMessage extends RecyclerView.ViewHolder {

            public com.github.library.bubbleview.BubbleTextView text;
            public receivedMessage(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.receivedBubble);
            }
            public void bind(Talk talk){
                text.setText(talk.getMsg());
            }
        }
    }
    public void refreshData(){
        databaseReference.addChildEventListener(new ChildEventListener() {
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
    class loadConversation extends AsyncTask<Void,Void,Void>{

        DataSnapshot dataSnapshot;
        public loadConversation(DataSnapshot ds){
            dataSnapshot = ds;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            initConversation(dataSnapshot);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initChatlog();

        }
    }
}
