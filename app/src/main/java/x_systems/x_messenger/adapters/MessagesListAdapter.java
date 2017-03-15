package x_systems.x_messenger.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.storage.MessagesList;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class MessagesListAdapter extends ArrayAdapter<String> {
    private MessagesList messagesList;
    private final static int resourceId = R.layout.adapter_messages_list;

    public MessagesListAdapter(Context context, MessagesList messagesList) {
        super(context, resourceId, messagesList.names);
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((BaseActivity)getContext()).getLayoutInflater();
        View rowView = inflater.inflate(resourceId, null, true);

        TextView time = (TextView) rowView.findViewById(R.id.time);
        TextView month_day = (TextView) rowView.findViewById(R.id.month_day);
        Date date = new Date(messagesList.unixTimes.get(position));
        SimpleDateFormat hoursMinutes = new SimpleDateFormat("HH:mm");
        SimpleDateFormat monthsDays = new SimpleDateFormat("MM/dd");
        time.setText(hoursMinutes.format(date));
        month_day.setText(monthsDays.format(date));

        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView message = (TextView) rowView.findViewById(R.id.message);
        if (!messagesList.isSenders.get(position))
        {
            name.setTextColor(getColor(R.color.colorPrimaryDark));
            message.setTextColor(getColor(R.color.colorUser));
        }
        name.setText(messagesList.names.get(position));
        message.setText(messagesList.messages.get(position));

        return rowView;
    }

    public void add(Long unixTime, String name, Boolean isSender, String message) {
        messagesList.unixTimes.add(unixTime);
        messagesList.names.add(name);
        messagesList.isSenders.add(isSender);
        messagesList.messages.add(message);
    }

    private int getColor(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.M) {
            return getContext().getResources().getColor(id, null);
        } else {
            return getContext().getResources().getColor(id);
        }
    }
}
