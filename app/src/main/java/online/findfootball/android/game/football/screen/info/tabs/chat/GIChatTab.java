package online.findfootball.android.game.football.screen.info.tabs.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import online.findfootball.android.R;
import online.findfootball.android.app.App;
import online.findfootball.android.firebase.database.FBDatabase;
import online.findfootball.android.firebase.database.children.PackableArrayList;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.chat.Mailbox;
import online.findfootball.android.game.chat.MessageObj;
import online.findfootball.android.game.chat.OutComingMessage;
import online.findfootball.android.game.chat.PostBot;
import online.findfootball.android.game.football.screen.info.tabs.chat.recyclerview.ChatAdapter;
import online.findfootball.android.user.AppUser;

public class GIChatTab extends Fragment implements Mailbox {

    private static final String TAG = App.G_TAG + ":GIChatTab";

    private GameObj thisGameObj;
    private AppUser appUser;
    private PostBot postBot;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ImageButton sendMsgButton;
    private EditText msgEditText;

    public GIChatTab() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gi_tab_chat, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        sendMsgButton = (ImageButton) rootView.findViewById(R.id.send_msg_btn);
        msgEditText = (EditText) rootView.findViewById(R.id.msg_edit_text);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        msgEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return rootView;
    }

    private void sendMessage() {
        String msgText = msgEditText.getText().toString().trim();
        if (thisGameObj != null && !msgText.isEmpty()) {
            if (appUser == null) {
                appUser = AppUser.getInstance(getContext(), true);
            }
            if (appUser != null) {
                MessageObj msg = new MessageObj();
                msg.setText(msgText);
                msg.setUserFrom(appUser);
                if (chatAdapter != null) {
                    chatAdapter.addMessage(new OutComingMessage(msg));
                }
                PackableArrayList<MessageObj> packableArrayList = thisGameObj.getChat();
                packableArrayList.add(msg); // добавляем игру к ивенту

                // отправляем сообщение в базу данных
                // save() на PackableArrayList не вызываем для ускоренного сохранения
                // во избежании очистки и перезаписи ArrayList'a
                msg.pack(FBDatabase.getDatabaseReference(packableArrayList)
                        .child(String.valueOf(msg.getTime())));
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                msgEditText.setText("");
            }
        } else {
            msgEditText.setText("");
        }
    }


    public void setData(GameObj game) {
        this.thisGameObj = game;
        if (appUser == null) {
            appUser = AppUser.getInstance(getContext(), true);
        }
        if (appUser != null) {
            startListening();
        }
    }


    private void startListening() {
        if (thisGameObj == null) {
            return;
        }
        if (postBot == null) {
            postBot = new PostBot(thisGameObj.getChat());
            postBot.setMailbox(this);
        } else if (postBot.isLoading()) {
            return;
        }

        postBot.startListening();
    }

    private void stopListening() {
        if (postBot != null) {
            postBot.stopListening();
        }
    }


    @Override
    public void onMessageReceived(int pos, @NotNull MessageObj msg) {
        //Log.d(TAG, "onComplete: " + "[" + pos + "]: " + msg);
        if (chatAdapter != null) {
            if (appUser != null) {
                if (msg.getUserFrom().equals(appUser)) {
                    // Если полученое сообщение отправленно данным пользователем
                    int opos = chatAdapter.indexOf(msg);
                    if (opos != -1) {
                        // если сообщение уже было добавлено в RecyclerView
                        // обновляем его статус
                        chatAdapter.updateMessageStatus(opos, OutComingMessage.SendStatus.OK);
                    } else {
                        // добавляем в RecyclerView как исходящее
                        chatAdapter.addMessage(new OutComingMessage(msg));
                    }
                } else {
                    // получено входящее сообщение
                    // добавляем в RecyclerView
                    chatAdapter.addMessage(msg);
                    if (recyclerView != null) {
                        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }
            }
        }
    }
}
