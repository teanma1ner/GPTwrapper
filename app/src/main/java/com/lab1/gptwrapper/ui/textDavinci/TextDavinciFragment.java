package com.lab1.gptwrapper.ui.textDavinci;

import android.os.Bundle;
import android.util.Log;
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
import com.lab1.gptwrapper.OpenAIAPI;
import com.lab1.gptwrapper.TextDavinciRecyclerViewAdapter;
import com.lab1.gptwrapper.databinding.FragmentTextDavinciBinding;

import java.util.ArrayList;
import java.util.List;

public class TextDavinciFragment extends Fragment {

    private FragmentTextDavinciBinding binding;
    private ImageButton sendButton;
    private EditText messageEditText;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextDavinciRecyclerViewAdapter mAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TextDavinciViewModel galleryViewModel =
                new ViewModelProvider(this).get(TextDavinciViewModel.class);

        binding = FragmentTextDavinciBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // установка слушателя ответов от API
        App.getInstance().getopenAIAPI().setOnResponseReceivedTextDavinciListener(onResponseReceivedTextDavinciListener);

        // setting up RecyclerView
        mRecyclerView = binding.textDavinciListRecyclerView;
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //--
        List<TextDavinciRecyclerViewAdapter.MessageItem> messageItemDataset = new ArrayList<>();
        // setting up adapter
        mAdapter = new TextDavinciRecyclerViewAdapter(messageItemDataset);
        mRecyclerView.setAdapter(mAdapter);

        // send button click listener
        sendButton = binding.sendButton;
        messageEditText = binding.messageEditText;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String requestText = messageEditText.getText().toString();
                messageItemDataset.add(new TextDavinciRecyclerViewAdapter.MessageItem("User", requestText, "Text-davinci", ""));
                messageEditText.setText("");
                mAdapter.notifyItemChanged(messageItemDataset.size() - 1);
                int itemCount = mAdapter.getItemCount();
                mRecyclerView.smoothScrollToPosition(itemCount - 1);

                //создание потока для запроса к API openAI
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Boolean ans = App.getInstance().getopenAIAPI()
                                    .davinciResponse(requestText);

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

    private OpenAIAPI.OnResponseReceivedTextDavinciListener onResponseReceivedTextDavinciListener = new OpenAIAPI.OnResponseReceivedTextDavinciListener() {
        @Override
        public void onResponseReceivedTextDavinci(String response) {
            mAdapter.addResponseToEndElement(response);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}