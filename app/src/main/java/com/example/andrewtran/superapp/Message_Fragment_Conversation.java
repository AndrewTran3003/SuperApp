package com.example.andrewtran.superapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.TAG;

public class Message_Fragment_Conversation extends Fragment implements Serializable {
    private View view;
    private ArrayList<Talks> listOfTalks;
    private ArrayList<Talks> listOfUserTalks;
    private List<Conversation> conversationList;
    private Button addConversation;
    Button logOut;
    private DatabaseReference conversationData = FirebaseDatabase.getInstance("https://composed-hangar-219019-c4dcc.firebaseio.com/").getReference();
    String userEmail;
    RecyclerView rvAllConversation;

    Message_Fragment_Conversation.CustomRowAdapter customRowAdapter;
    RecyclerView.LayoutManager layoutManager;

    TextView welcomeText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.message_fragment_conversation,container,false);
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        welcomeText = view.findViewById(R.id.welcomeText);
        welcomeText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        addConversation = view.findViewById(R.id.addConversationButton);
        logOut = view.findViewById(R.id.logOutButton);
        initSignOutButton();
        initUI();
        initAddConversationButton();
        return view;
    }



    private void initUI(){
        rvAllConversation = view.findViewById(R.id.allConversation);
        layoutManager = new LinearLayoutManager(getContext());
        rvAllConversation.setLayoutManager(layoutManager);

        Log.d(TAG, "onDataChange 1: ");
        conversationData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                new getData(dataSnapshot).execute();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        refreshData();
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
                for (DataSnapshot conversationDetail3: conversationDetail2.child("Log").getChildren()){
                    Talk talk = conversationDetail3.getValue(Talk.class);
                    talks.addTalk(talk);

                }

                for (DataSnapshot conversationDetail3: conversationDetail2.child("Overview").getChildren()){
                    ArrayList<String> result =(ArrayList<String>) conversationDetail3.getValue();
                    talks.addPeople(result);
                }
                listOfTalks.add(talks);
            }
        }
    }
    private void initUserConversation(){
        listOfUserTalks = new ArrayList<>();
        for (Talks talks : listOfTalks){
            if (talks.getPeople().indexOf(userEmail) >= 0){
                listOfUserTalks.add(talks);
            }
        }
    }
    private void initConversationList(){
        conversationList = new ArrayList<>();
        for (Talks talks : listOfUserTalks){

            String person = talks.getPeople().get(1);
            for (String otherPerson:talks.getPeople()){
                if(otherPerson.trim() == userEmail.trim()){
                    continue;

                }
                else{
                    person = otherPerson;
                }
            }
            int lastTextIndex = talks.getTalks().size();
            String lastText = talks.getTalks().get(lastTextIndex-1).getMsg();
            String talkID = talks.getTalkID();
            Conversation conversation = new Conversation(talkID,person,lastText);
            conversationList.add(conversation);
        }

    }
    private void initListOfConversation(){
        customRowAdapter = new CustomRowAdapter(conversationList);
        rvAllConversation.setAdapter(customRowAdapter);

    }
    private void initSignOutButton(){

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOutActivity();
            }
        });
    }

    private void SignOutActivity(){
        FirebaseAuth.getInstance().signOut();

        welcomeText.setText("You are now logged out!");
        addConversation.setVisibility(View.INVISIBLE);
        rvAllConversation.setVisibility(View.INVISIBLE);
        logOut.setVisibility(View.INVISIBLE);

    }

    private void initAddConversationButton(){
        addConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Message_Activity_AddConversation.class);
                startActivityForResult(intent,1);
            }
        });

    }

    class CustomRowAdapter extends RecyclerView.Adapter<Message_Fragment_Conversation.CustomRowAdapter.ViewHolder>{
        private List<Conversation> conversationArrayList;

        public CustomRowAdapter(List<Conversation> conversationArrayList) {
            this.conversationArrayList = conversationArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View v = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_conversation,viewGroup,false);
            Message_Fragment_Conversation.CustomRowAdapter.ViewHolder vh = new Message_Fragment_Conversation.CustomRowAdapter.ViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = rvAllConversation.getChildAdapterPosition(view);
                    Conversation conversation = conversationArrayList.get(position);
                    Intent intent = new Intent(getContext(),Message_Activity_ChatRoom.class);
                    intent.putExtra("selected_conversation",conversation.getTalkID());
                    startActivityForResult(intent,1);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Conversation conversation = conversationArrayList.get(i);
            viewHolder.person.setText(conversation.getPerson());
            viewHolder.lastText.setText(conversation.getLastText());

        }


        @Override
        public int getItemCount() {
            return conversationArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView person;
            public TextView lastText;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                person = itemView.findViewById(R.id.person);
                lastText = itemView.findViewById(R.id.lastText);
            }
        }
    }
    public void refreshData(){
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
    class getData extends AsyncTask<DataSnapshot,Void,List<Talks>>{

       private DataSnapshot dataSnapshot;
        public getData(DataSnapshot DataSnapshot){
            dataSnapshot = DataSnapshot;
        }



        @Override
        protected List<Talks> doInBackground(DataSnapshot... dataSnapshots) {
            listOfTalks = new ArrayList<>();
            for (DataSnapshot conversationDetail: dataSnapshot.getChildren())
            {
                Log.d(TAG, "initAllConversation: "+conversationDetail);
                for (DataSnapshot conversationDetail2: conversationDetail.getChildren()){
                    String talkID = conversationDetail2.getKey();
                    Talks talks = new Talks();
                    talks.setTalkID(talkID);
                    for (DataSnapshot conversationDetail3: conversationDetail2.child("Log").getChildren()){
                        Talk talk = conversationDetail3.getValue(Talk.class);
                        talks.addTalk(talk);

                    }

                    for (DataSnapshot conversationDetail3: conversationDetail2.child("Overview").getChildren()){
                        ArrayList<String> result =(ArrayList<String>) conversationDetail3.getValue();
                        talks.addPeople(result);
                    }
                    listOfTalks.add(talks);
                }
            }
            return listOfTalks;
        }

        @Override
        protected void onPostExecute(List<Talks> talks) {
            initUserConversation();
            initConversationList();
            initListOfConversation();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
