package com.lab1.gptwrapper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TextDavinciRecyclerViewAdapter extends RecyclerView.Adapter<TextDavinciRecyclerViewAdapter.MyViewHolder>{
    static public class MessageItem {
        public String authorTop;
        public String messageTextTop;
        public String authorBottom;
        public String messageTextBottom;

        public MessageItem(String authorTop, String messageTextTop, String authorBottom, String messageTextBottom) {
            this.authorTop = authorTop;
            this.messageTextTop = messageTextTop;
            this.authorBottom = authorBottom;
            this.messageTextBottom = messageTextBottom;
        }
    }
    private List<TextDavinciRecyclerViewAdapter.MessageItem> messageItemDataset;

    public List<TextDavinciRecyclerViewAdapter.MessageItem> getMessageItemList() {return messageItemDataset;}

    public TextDavinciRecyclerViewAdapter(List<TextDavinciRecyclerViewAdapter.MessageItem> messageItemList){
        messageItemDataset = messageItemList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView authorTextViewTop;
        public TextView messageTextViewTop;
        public TextView authorTextViewBottom;
        public TextView messageTextViewBottom;
        public MyViewHolder(View v){
            super(v);
            authorTextViewTop = v.findViewById(R.id.textViewAuthorTop);
            messageTextViewTop = v.findViewById(R.id.textViewMessageTop);
            authorTextViewBottom = v.findViewById(R.id.textViewAuthorBottom);
            messageTextViewBottom = v.findViewById(R.id.textViewMessageBottom);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){


                }
            });
        }
    }


    @Override
    public TextDavinciRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_davinci_message_element, parent, false);

        TextDavinciRecyclerViewAdapter.MyViewHolder vh = new TextDavinciRecyclerViewAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(TextDavinciRecyclerViewAdapter.MyViewHolder holder, int position){
        TextDavinciRecyclerViewAdapter.MessageItem curItem = messageItemDataset.get(position);
        holder.authorTextViewTop.setText(curItem.authorTop);
        holder.messageTextViewTop.setText(curItem.messageTextTop);
        holder.authorTextViewBottom.setText(curItem.authorBottom);
        holder.messageTextViewBottom.setText(curItem.messageTextBottom);
    }

    @Override
    public int getItemCount() {return messageItemDataset.size();}

    public void addMessage(TextDavinciRecyclerViewAdapter.MessageItem newMessage) {
        messageItemDataset.add(newMessage);
    }
    public void addResponseToEndElement(String response) {
        if (messageItemDataset.size() != 0) {
            int lastItemPosition = messageItemDataset.size() - 1;

            MessageItem cur = messageItemDataset.get(lastItemPosition);
            cur.messageTextBottom = String.join("", cur.messageTextBottom, response);
            messageItemDataset.remove(lastItemPosition);
            messageItemDataset.add(cur);
            notifyItemChanged(lastItemPosition);
        }
    }
}
