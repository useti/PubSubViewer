package jab;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ytihoglaz
 * Date: 27.08.13
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public interface NewsArrivedEvent {
    public void HandleNews(String feedID);
}
