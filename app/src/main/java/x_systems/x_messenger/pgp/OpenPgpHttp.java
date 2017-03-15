package x_systems.x_messenger.pgp;

import com.pawelgorny.pgpotr.PgpService;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Manasseh on 11/15/2016.
 */

public class OpenPgpHttp {
    static int a = 0;
    public static String addkey(String key){
        String output = "";
        try {
            List<String> sites = Keysites();

            String data = URLEncoder.encode("keytext", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8") ;
            for( a = a;a < sites.size();a++) {
                URL url = new URL("http://" + sites.get(a) + "/pks/add");
                System.out.println("doing stuff " + sites.get(a));
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setDoOutput(true);
                BufferedWriter os = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                os.write(data);
                os.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

                char temp[] = new char[1024];
                String mytempstr = "";
                br.read(temp);
                for (char l : temp) {
                    if (l != (char) 0)
                        output += l;
                    mytempstr += l;
                }
                System.out.println("output of site " + sites.get(a) + ": " + mytempstr);
                br.close();
                os.close();
                client.disconnect();
            }
        } catch (MalformedURLException e) {
            a++;
            addkey(key);
            return output;
        } catch (IOException e) {
            a++;
            addkey(key);
            return output;
        }

        System.out.println("finished doing stuff");
        a = 0;
        return output;
    }
    public static List<String>Keysites() {
        List<String> output = new ArrayList<>();
        try {
            org.jsoup.nodes.Document sites = Jsoup.connect("https://sks-keyservers.net/overview-of-pools.php").get();
            output.add("sks.spodhuis.org");
            output.add("keyserver.searchy.nl:11371");
            for(int a = 1; a < sites.getElementsByTag("h2").size()-1;a++) {
                output.add(sites.getElementsByTag("h2").get(a).html());
            }
            return output;
        } catch (IOException e) {
            Keysites();
        }
        return null;
    }

    public static String getkey(String fp){
        try {

            List<String> fingerprints = new ArrayList<>();
            String output = "";
            org.jsoup.nodes.Document doc = Jsoup.connect("http://sks.spodhuis.org/pks/lookup?op=get&search=0x" + fp ).get();
            Elements pres = doc.getElementsByTag("pre");

            return pres.get(0).html()
                    .replace(pres.get(0).html().split("\n")[1], "")
                    .replace(pres.get(0).html().split("\n")[2], "")
                    ;



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getmykeyid(PgpService svc, String jid){
        try {

            URL url = new URL("https://sks-keyservers.net/pks/lookup?op=index&search=" + jid);
            HttpURLConnection client = (HttpURLConnection)url.openConnection();
            client.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String output = "";
            char temp[] = new char[10240];
            br.read(temp);
            for(char l : temp){
                if(l != (char)0)
                    output += l;
            }

            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(output));
            Document doc = dBuilder.parse(is);

            for(int a = 0;a < doc.getElementsByTagName("a").getLength();a++) {

                System.out.println("length " + output);
            }
            return output;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return null;
    }
}
