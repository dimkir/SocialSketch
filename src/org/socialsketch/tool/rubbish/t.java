package org.socialsketch.tool.rubbish;

import java.util.ArrayList;
import java.util.Date;
import processing.core.PApplet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * some twitter sketch example.
 */
public class t extends PApplet {

    ConfigurationBuilder cb;
    Twitter twitter;

    

    
    @Override
    public void setup() {

        twitter = new TwitterFactory(cb.build()).getInstance();

        Query query = new Query("#processing");
        // query.setRpp(100);

        try {
            QueryResult result = twitter.search(query);
            ArrayList tweets = (ArrayList) result.getTweets();

            for (int i = 0; i < tweets.size(); i++) {
                Status t = (Status) tweets.get(i);
                String user = t.getUser().getScreenName();
                String msg = t.getText();
                Date d = t.getCreatedAt();
                println("Tweet by " + user + " at " + d + ": " + msg);
            };
        } catch (TwitterException te) {
            println("Couldn't connect: " + te);
        };
    }

    @Override
    public void draw() {
    }
}
