package x_systems.x_messenger.pgp;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by Manasseh on 11/18/2016.
 */

public class PgpExtension implements ExtensionElement {
    private String dataToSend = "";
    public PgpExtension(String dataToSend) {
        this.dataToSend = dataToSend;
    }
    @Override
    public String getNamespace() {
        return "jabber:x:encrypted";
    }

    @Override
    public String getElementName() {
        return "x";
    }

    @Override
    public CharSequence toXML() {
        return "<" + getElementName() + " xmlns=\"" + getNamespace() + "\">" +
                dataToSend +
                "</" + getElementName() + ">";
    }
}
