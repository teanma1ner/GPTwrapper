package com.lab1.gptwrapper.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lab1.gptwrapper.App;
import com.lab1.gptwrapper.ChatRecyclerViewAdapter;
import com.lab1.gptwrapper.OpenAIAPI;
import com.lab1.gptwrapper.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ImageButton sendButton;
    private EditText messageEditText;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ChatRecyclerViewAdapter mAdapter;
    Handler mainHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel homeViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);
        // надувание макета
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Установка хендлера основного потока
        mainHandler = new Handler(Looper.getMainLooper());
        // установка слушателя ответов от API
        App.getInstance().getopenAIAPI().setOnResponseReceivedChatGPTListener(onResponseReceivedChatGPTListener);


        // setting up RecyclerView
        mRecyclerView = binding.chatListRecyclerView;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //--
        List<ChatRecyclerViewAdapter.MessageItem> messageItemDataset = new ArrayList<>();;
        //messageItemDataset.add(new ChatRecyclerViewAdapter.MessageItem("chatGPT", "tdmeme", false));
        //messageItemDataset.add(new ChatRecyclerViewAdapter.MessageItem("user", "bwbw", true));
        //--
        // setting up adapter
        mAdapter = new ChatRecyclerViewAdapter(messageItemDataset);
        mRecyclerView.setAdapter(mAdapter);

        // send button click listener
        sendButton = binding.sendButton;
        messageEditText = binding.messageEditText;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageEditText.getText().toString();
                messageEditText.setText("");
                messageItemDataset.add(new ChatRecyclerViewAdapter.MessageItem("User", userMessage, true));
                messageItemDataset.add(new ChatRecyclerViewAdapter.MessageItem("ChatGPT", "", false));
                int lastItemPosition = mAdapter.getItemCount() - 1;
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(lastItemPosition);

                //создание потока для запроса к chatGPT
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Boolean ans = App.getInstance().getopenAIAPI()
                            //        .chatGPTResponse(userMessage, "You are a helpful assistant.", 0.5);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
                thread.start();

            }
        });


        return root;
    }

    private OpenAIAPI.OnResponseReceivedChatGPTListener onResponseReceivedChatGPTListener = new OpenAIAPI.OnResponseReceivedChatGPTListener() {
        @Override
        public void onResponseReceivedChatGPT(String response) {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addResponseToEndElement(response); // mAdapter может не существовать
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}