package jab;

import org.jivesoftware.smackx.pubsub.PayloadItem;

import java.net.URL;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 21.08.13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public interface IRss {
    // ID
    public String getId();

    // Title
    public String getTitle();

    // Link
    public String getLink();

    // PDate
    public Date getPDate();

    // Author
    public String getAuthor();

    // Description
    public String getDescription();

    public boolean getUnread();
    public void setUnread(boolean state);

//    public PayloadItem genPayload();
}
