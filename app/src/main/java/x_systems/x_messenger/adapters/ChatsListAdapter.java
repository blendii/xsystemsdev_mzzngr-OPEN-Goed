package x_systems.x_messenger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.storage.ChatsList;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class ChatsListAdapter extends ArrayAdapter<String> {
    private ChatsList chatsList;
    private final static int resourceId = R.layout.adapter_chats_list;

    public ChatsListAdapter(Context context, ChatsList chatsList) {
        super(context, resourceId, chatsList.contactNames);
        this.chatsList = chatsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((BaseActivity)getContext()).getLayoutInflater();
        View rowView = inflater.inflate(resourceId, null, true);

//        ImageView contactIcon = (ImageView) rowView.findViewById(R.id.contactIcon);
//        contactIcon.setImageResource(chatsList.photos.get(position));

        TextView contactName = (TextView) rowView.findViewById(R.id.contactName);
        contactName.setText(chatsList.contactNames.get(position));

        ImageView encryptionType = (ImageView) rowView.findViewById(R.id.encryptionType);
        encryptionType.setImageResource(chatsList.encryptionTypes.get(position).getResourceId());

        TextView dateTime = (TextView) rowView.findViewById(R.id.dateTime);
        dateTime.setText(chatsList.dateTimes.get(position));

        return rowView;
    }
}
