package com.lab1.gptwrapper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.MyViewHolder> {

    static public class MessageItem {
        public String author;
        public String messageText;
        public boolean gravity; // 0 - left, 1 - right

        public MessageItem(String author, String messageText, boolean gravity) {
            this.author = author;
            this.messageText = messageText;
            this.gravity = gravity;
        }
    }
    private List<MessageItem> messageItemDataset;


    public List<MessageItem> getMessageItemList() {return messageItemDataset;}

    public ChatRecyclerViewAdapter(List<MessageItem> messageItemList){
        messageItemDataset = messageItemList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout messageIconLayout;
        public TextView authorTextView;
        public TextView messageTextView;
        public MyViewHolder(View v){
            super(v);
            messageIconLayout = v.findViewById(R.id.messageIcon);
            authorTextView = v.findViewById(R.id.textViewAuthor);
            messageTextView = v.findViewById(R.id.textViewMessage);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){


                }
            });
        }
    }


    @Override
    public ChatRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_element, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        MessageItem curItem = messageItemDataset.get(position);
        holder.messageTextView.setText(curItem.messageText);
        holder.authorTextView.setText(curItem.author);

        // изменение гравитации layout в зависимости от параметра
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.messageIconLayout.getLayoutParams();
        if (curItem.gravity) {
            // Выравнивание вправо
            layoutParams.gravity = Gravity.END;
        } else {
            // Выравнивание влево
            layoutParams.gravity = Gravity.START;
        }
        holder.messageIconLayout.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {return messageItemDataset.size();}

    public void addMessage(MessageItem newMessage) {
        messageItemDataset.add(newMessage);
    }

    public void addResponseToEndElement(String response) {
        if (messageItemDataset.size() != 0) {
            int lastItemPosition = messageItemDataset.size() - 1;

            ChatRecyclerViewAdapter.MessageItem cur = messageItemDataset.get(lastItemPosition);
            cur.messageText = String.join("", cur.messageText, response);
            messageItemDataset.remove(lastItemPosition);
            messageItemDataset.add(cur);
            notifyItemChanged(lastItemPosition);
        }
    }

}

