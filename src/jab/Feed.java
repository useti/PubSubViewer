package jab;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 26.08.13
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
public class Feed {
    public Feed(String name, JabberClient jabber) {
        this.name = name;
        this.jabber = jabber;
    }

    public String getName() {
        return name;
    }

    private final String name;
    private final JabberClient jabber;

    public Map<String, IRss> getItems() {
        return items;
    }

    public Map<String,IRss> items;

    public List<String> getItem_names() {
        return item_names;
    }

    public List<String> item_names;
}
