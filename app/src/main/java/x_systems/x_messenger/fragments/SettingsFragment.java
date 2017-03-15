package x_systems.x_messenger.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.adapters.ImagePickerAdapter;
import x_systems.x_messenger.application.Values;
import x_systems.x_messenger.fragments.support.v4.SupportFragment;
import x_systems.x_messenger.preferences.PreferenceLoader;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;
import x_systems.x_messenger.xmpp.XMPP;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class SettingsFragment extends SupportFragment {
    public static long auto_lock_time_opened;

    private PreferenceLoader preferenceLoader;
    private Database database;

    private Spinner statusFromDropDown;
    private EditText statusFromOwn;
    private EditText whipe_out_password;
    private Spinner auto_lock;
    private Spinner message_self_destruct;
    private EditText otr_public_mail;
    private EditText pgp_public_mail;
    private EditText userName;

    private boolean fromDropDown = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_settings, container, false);
        preferenceLoader = new PreferenceLoader(getActivity());
        database = new Database(getActivity());



//        userName = (EditText)content.findViewById(R.id.userName);
//        try {
//            userName.setText(database.readProperty(Property.Type.USERNAME));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }





        whipe_out_password = (EditText)content.findViewById(R.id.whipe_out_password);

        auto_lock = (Spinner)content.findViewById(R.id.auto_lock);
        String auto_lock_time = String.valueOf(preferenceLoader.getPreferences(PreferenceLoader.Type.AUTO_LOCK_TIME));
        auto_lock.setSelection(((ArrayAdapter)auto_lock.getAdapter()).getPosition(auto_lock_time));

        message_self_destruct = (Spinner)content.findViewById(R.id.message_self_destruct);
        String message_self_destruct_time = String.valueOf(preferenceLoader.getPreferences(PreferenceLoader.Type.MESSAGE_DESTRUCTION_TIME));
        message_self_destruct.setSelection(((ArrayAdapter)message_self_destruct.getAdapter()).getPosition(message_self_destruct_time));

        otr_public_mail = (EditText)content.findViewById(R.id.otr_public_mail);
        pgp_public_mail = (EditText)content.findViewById(R.id.pgp_public_mail);
        try {
            otr_public_mail.setText(database.readProperty(Property.Type.USER_JID));
            pgp_public_mail.setText(Values.User + "@" + Values.Mail_Domain);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button set_password = (Button)content.findViewById(R.id.set_password);
        set_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = layoutInflater.inflate(R.layout.dialog_password, null);
                final EditText old_password = (EditText)content.findViewById(R.id.old_password);
                final EditText new_password = (EditText)content.findViewById(R.id.new_password);
                final EditText confirm_password = (EditText)content.findViewById(R.id.confirm_password);
                builder.setView(content);
                builder.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        boolean changePassword = true;
                        try {
                            if (!Objects.equals(old_password.getText().toString(), database.readProperty(Property.Type.ENCRYPTED_PASSWORD)))
                                changePassword = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!Objects.equals(new_password.getText().toString(), confirm_password.getText().toString()))
                            changePassword = false;

                        if (new_password.getText().toString().length() < 6) {
                            changePassword = false;
                            new_password.setError("Should be longer than 6 characters!");
                        }

                        if (changePassword) {
                            try {
                                new XMPP().changePassword(new_password.getText().toString());
                                database.writeProperty(Property.Type.ENCRYPTED_PASSWORD, new_password.getText().toString());
                                dialog.dismiss();
                                dialog.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                                confirm_password.setError("Password can not be changed at this time.");
                            }
                        }
                        else {
                            old_password.setError("Old or new password is incorrect!");
                        }
                    }
                });
            }
        });

        Button pgp_public_key = (Button)content.findViewById(R.id.pgp_public_key);
        pgp_public_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setMessage(((BaseActivity)getActivity()).pgpStarter.pgpService.getMyPublicKey().getResultString().replace("\n", ""));
                dialog.setTitle("Pgp PublicKey");
                dialog.setCancelable(true);
                dialog.create().show();
            }
        });

        Button private_key = (Button)content.findViewById(R.id.private_key);
        private_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setMessage(((BaseActivity)getActivity()).pgpStarter.pgpService.getMyPrivateKeyArmoredBlock().getResultString().replace("\n", ""));
                dialog.setTitle("Pgp PrivateKey");
                dialog.setCancelable(true);
                dialog.create().show();
            }
        });

        Button pgp_qr_code = (Button)content.findViewById(R.id.pgp_qr_code);
        pgp_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View content = getActivity().getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
                ImageView qr_code = (ImageView) content.findViewById(R.id.qr_code);

                String pgpFingerPrint = "OPENPGP4FPR:";
                pgpFingerPrint += ((BaseActivity)getActivity()).pgpStarter.pgpService.getMyKeyMetadata().getKeyData().getFingerprintFormatted();

                try {
                    qr_code.setImageBitmap(generateQrCode(pgpFingerPrint));
                } catch (WriterException e) {
                    Toast.makeText(getActivity(), "Failed To Generate Qr Code.", Toast.LENGTH_LONG);
                    e.printStackTrace();
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(content);
                //dialog.setMessage(pgpFingerPrint);
                dialog.setTitle("Pgp QR Code");
                dialog.setCancelable(true);
                dialog.create().show();
            }
        });

        Button save = (Button)content.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!whipe_out_password.getText().toString().isEmpty())
                {
                    String password = whipe_out_password.getText().toString();
                    new Database(getActivity()).writeProperty(Property.Type.WHIPE_OUT_PASSWORD, password);
                }

//                ((TextView)getActivity().findViewById(R.id.toolbar).findViewById(R.id.profileName)).setText(((EditText)getView().findViewById(R.id.userName)).getText().toString());

                preferenceLoader.setAutoLockTime(Long.valueOf(auto_lock.getSelectedItem().toString()));

                if (Objects.equals(auto_lock.getSelectedItem().toString(), "NEVER"))
                    preferenceLoader.setMessageDestructionTime(0L);
                else
                    preferenceLoader.setMessageDestructionTime(Long.valueOf(message_self_destruct.getSelectedItem().toString()));


                Toast.makeText(getActivity(), "Settings saved.", Toast.LENGTH_LONG).show();
            }
        });
        return content;
    }

    private Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 256;

        BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}