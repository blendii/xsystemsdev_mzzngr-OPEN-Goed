package x_systems.x_messenger.fragments;

/**
 * Created by Manasseh on 10/8/2016.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QDecoderStream;
import com.sun.mail.util.QPDecoderStream;

import org.apache.harmony.awt.internal.nls.Messages;
import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.mail.util.SharedByteArrayInputStream;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.adapters.ChatsListAdapter;
import x_systems.x_messenger.fragments.support.v4.SupportFragment;
import x_systems.x_messenger.listeners.NavigateListener;
import x_systems.x_messenger.pgp.PGPMailMessage;
import x_systems.x_messenger.storage.ChatsList;
import x_systems.x_messenger.storage.Database;

public class ChatsFragment extends SupportFragment {

    NavigateListener navigateListener;
    private ChatsListAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            navigateListener = (NavigateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnContactFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CreateChatsListWithOnItemClickListener();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            System.out.println("UNHIDDEN");
            CreateChatsListWithOnItemClickListener();
            adapter.notifyDataSetChanged();
        } else {
            System.out.println("HIDDEN");
        }
    }

    private void CreateChatsListWithOnItemClickListener()
    {
        final ChatsList chatsList = new Database(getActivity()).getChatsList();

        adapter = new ChatsListAdapter(getActivity(), chatsList);
        ListView listChats = (ListView)getView().findViewById(R.id.listChats);
        listChats.setAdapter(adapter);
        listChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String contactJID = chatsList.JIDs.get(position);
                    String contactName = chatsList.contactNames.get(position);
                    Integer resourceId = chatsList.encryptionTypes.get(position).getResourceId();
                    if(resourceId == R.drawable.pgp) {
                        GoToChatFragment(contactJID, contactName, resourceId);
                    }
                    else
                        GoToChatFragment(contactJID, contactName, resourceId);
                }
            });
    }

    private void GoToChatFragment(final String JID, final String contactName, final Integer resourceId)
    {
        if (navigateListener != null)
            navigateListener.OnRequestNavigate("chat", new Object[] {JID, contactName, resourceId});
    }






}