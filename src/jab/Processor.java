package jab;

import org.jivesoftware.smack.XMPPException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Processor implements Runnable{

    public static Builder newBuilder(JabberClient jabber) {
        return new Builder(jabber);
    }

    public void activate() {
        new Thread(this, "Process").start();
    }


    private Processor(Builder bld) {
        this.pr = bld;
    }


    @Override
    public void run() {

        while(true) {
            try {
                procesMessages();
                Thread.sleep(pr.interval_secs * 1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private BigInteger procesMessages() throws InterruptedException{
        BigInteger result = new BigInteger("-1");
        try {
            pr.jabber.connect();
            pr.jabber.sendMessage("ytihoglaz@useti.ru","ping");
            pr.jabber.disconnect();
        }
        catch (XMPPException e) {
            e.printStackTrace();
        }

        return result;
    }

    private Builder pr = null;

    public static class Builder {

        private Builder(JabberClient jabber) {
            this.jabber = jabber;
        }

        public Processor build() {
            if (jabber == null)
                throw new IllegalStateException("one of mandatory params not good");

            return new Processor(this);
        }

        public Builder interval(int interval_secs) {
            this.interval_secs = interval_secs;
            return this;
        }

        public Builder to_jids(List<String> to_jids) {
            this.to_jids = to_jids;
            return this;
        }

        public Builder jabber_delay(int jabber_delay) {
            this.jabber_delay = jabber_delay;
            return this;
        }

        public Builder rt_enable(boolean rt_enable) {
            this.no_rt = !rt_enable;
            return this;
        }

        public Builder expand_links(boolean expand_links) {
            this.expand_links = expand_links;
            return this;
        }

        public Builder jprefix(String jprefix) {
            this.jprefix = jprefix;
            return this;
        }

        public Builder jsufix(String jsufix) {
            this.jsufix = jsufix;
            return this;
        }

        @Override
        public String toString() {
            return "Builder [jabber=" + jabber
                    + ", interval_secs="
                    + interval_secs + ", to_jids="
                    + to_jids + ", jabber_delay=" + jabber_delay + ", no_rt="
                    + no_rt + ", expand_links=" + expand_links + ", jprefix="
                    + jprefix + ", jsufix=" + jsufix + "]";
        }

        private final JabberClient jabber;

        private int interval_secs = 180;

        private List<String> to_jids = new ArrayList<String>();
        private int jabber_delay = 10;
        private boolean no_rt = true;
        private boolean expand_links = false;
        private String jprefix, jsufix;
    }

}

