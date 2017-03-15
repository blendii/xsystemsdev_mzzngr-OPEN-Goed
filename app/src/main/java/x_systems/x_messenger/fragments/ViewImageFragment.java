package x_systems.x_messenger.fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import x_systems.x_messenger.Encryption.AES256;
import x_systems.x_messenger.Encryption.ASP;
import x_systems.x_messenger.R;
import x_systems.x_messenger.activities.BaseActivity;
import x_systems.x_messenger.activities.ViewFile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewImageFragment extends Fragment {

    public ViewImageFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View main = inflater.inflate(R.layout.fragment_view_image, container, false);
        System.out.println(ViewFile.filePath+"/"+ ViewFile.filePath);


        try {



            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        final File f = new File(ViewFile.filePath);
                        //byte[] get = AES256.decryptedFileRead(f, "hello").getBytes(Charset.forName("ISO-8859-1"));
                        long tm = System.currentTimeMillis();
                        final byte[] decrypted = AES256.GetBytesFromString(ASP.DecryptFileToMemory(f));
                        System.out.println("TIME SPEND: " + (System.currentTimeMillis() - tm) + "ms");
                        System.out.println("IMAGE MESSAGE 2: " + AES256.GetStringFromBytes(decrypted));
                        final Bitmap bm = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length, options);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageView)main.findViewById(R.id.IvImageView)).setImageBitmap(bm);
                            }
                        });


                    } catch (Exception e) {
                        System.out.println("IMAGE MESSAGE ERROR " + e.toString());
                    }
                }
            }).start();

        } catch (Exception e) {
            System.out.println("ERROR 1214: " + e.toString());
        }



        return main;
    }

}
