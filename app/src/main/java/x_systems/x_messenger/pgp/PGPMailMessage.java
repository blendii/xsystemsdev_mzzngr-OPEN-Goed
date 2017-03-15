package x_systems.x_messenger.pgp;

import android.util.Base64;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QDecoderStream;
import com.sun.mail.util.QPDecoderStream;


import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.util.SharedByteArrayInputStream;



import static java.lang.System.in;

/**
 * Created by jeremiah8100 on 2-12-2016.
 */

public class PGPMailMessage {

    private String Subject;
    private String From;
    private String Content;
    private String Type;
    public PGPMailMessage(Message msg){
        this.Subject = "";
        try {
            this.From = getContactAddress(msg.getFrom()[0].toString());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        this.Content = getContent(msg);
        System.out.println("CT = " + this.Content);

    }

    private String getContactAddress(String unformatted) {
        int start = 0;
        int end = 0;
        for(int a = unformatted.length()-1;a > 0;a--){
            if(unformatted.toCharArray()[a] == '>') {
                end = a;
            } else if (unformatted.toCharArray()[a] == '<'){
                start = a + 1;
            }
        }
        return unformatted.substring(start, end);
    }

    public boolean IsSecure(){
        if(this.Content.contains("-----END PGP MESSAGE-----") && this.Content.contains("-----BEGIN PGP MESSAGE-----"))
            return true;
        return false;
    }

    private String getContent(Message msg) {
        byte[] hello = new byte[9000];
        String mytype = "NONE";
        try {

            if (msg.getContent() instanceof SharedByteArrayInputStream) {
                SharedByteArrayInputStream dt = (SharedByteArrayInputStream) msg.getContent();
                hello = new byte[dt.available()];
                dt.read(hello);
                mytype = "Shared";

            } else if (msg.getContent() instanceof QPDecoderStream) {
                QPDecoderStream dt = (QPDecoderStream) msg.getContent();

                dt.read(hello);
                mytype = "QPSTREAM";
            } else if (msg.getContent() instanceof QDecoderStream) {
                QDecoderStream dt = (QDecoderStream) msg.getContent();
                hello = new byte[dt.available()];
                dt.read(hello);
                mytype = "QSTREAM";
            } else if (msg.getContent() instanceof BASE64DecoderStream) {
                BASE64DecoderStream dt = (BASE64DecoderStream) msg.getContent();
                hello = new byte[dt.available()];
                dt.read(hello);
                mytype = "BASE64DecoderStream";

            } else {
                hello = msg.getContent().toString().getBytes();
            }
            this.Type = mytype;
            System.out.println("TYPE " + mytype);
            String fullmsg = new String(hello, "UTF-8");
            return FormatMessage(fullmsg, mytype);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String FormatMessage(String Message, String type) {
        String output = "";
        System.out.println("Formatting Shared");
       // if(type.equals("Shared")){
            String[] plain = Message.split("\n");
            int[] deny = { 0, 1, 2, plain.length -12,plain.length -11,plain.length -10, plain.length -9, plain.length -8, plain.length -7, plain.length -6, plain.length -5, plain.length -4, plain.length -3, plain.length - 2, plain.length - 1, plain.length};
            for(int a = 0; a < plain.length;a++){
                if(!IsInArray(a, deny)){
                    System.out.println(plain[a] + " num : " + a);
                    if(a != 3){
                        output += "\n";
                    }
                    if(!plain[a].contains("Content-Type:"))
                        output += plain[a];
                    else
                        return FinishFormat(output);
                }
            }
            return output;


       // }
        //return null;
    }

    private boolean IsInArray(int SearchItem, int[] array){
        for(int item : array){
            if(item == SearchItem)
                return true;
        }
        return false;
    }

    public String GetSubject(){
        return this.Subject;
    }

    public String GetSender(){
        return this.From;
    }

    public String GetMessage() {
        return new String(Base64.decode(this.Content, Base64.DEFAULT));
    }

    private String FinishFormat(String str){
        String[] splitted = str.split("\n");
        String output = "";
        for (int a = 0;a < splitted.length;a++){
            if(a != splitted.length - 1){
                output += splitted[a];
            }

        }
        return output;
    }
}
