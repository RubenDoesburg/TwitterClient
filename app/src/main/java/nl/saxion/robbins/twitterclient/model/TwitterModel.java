package nl.saxion.robbins.twitterclient.model;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/** Major twitter model that contains all other model objects.
 *
 * @author Niels Jan */
public class TwitterModel extends Observable implements Observer {

    /**********************************************************************************/
    /*********************** ENCAPSULATED INSTANCE VARIABLES **************************/
    /**********************************************************************************/

    private ArrayList<Tweet> tweets;
    private ArrayList<User> users;
    //private Profile profile;
    private HashSet<String> friendIds;
    private ArrayList<String> friendNames;
    private ArrayList<String> followerNames;

    /**********************************************************************************/
    /********************************** CONSTRUCTOR ***********************************/
    /**********************************************************************************/

    public TwitterModel() {
        // Initiate all lists / sets
        tweets = new ArrayList<>();
        users = new ArrayList<>();
        followerNames = new ArrayList<>();
        friendNames = new ArrayList<>();
        friendIds = new HashSet<>();
    }

    /**********************************************************************************/
    /************************************ GETTERS *************************************/
    /**********************************************************************************/

    /** Get the profile saved in this model */
    /*public Profile getProfile() {
        return profile;
    }*/

    /** Get the set with friend ids */
    public HashSet<String> getFriends() {
        return friendIds;
    }

    /** Get the list with tweets saved in this model */
    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    /** Get the list with follower names */
    public ArrayList<String> getfollowerNames() {
        return followerNames;
    }

    /** Get the list with friend names */
    public ArrayList<String> getFriendNames() {
        return friendNames;
    }

    /**********************************************************************************/
    /************************************ SETTERS *************************************/
    /**********************************************************************************/

    /** Add a friend to the friend ids list */
    public void addFriend(String friend) {
        friendIds.add(friend);
    }

    /** remove a friend from the friend ids list */
    public void removeFriend(String friend) {
        friendIds.remove(friend);
    }

    /** Set the tweet items with the JSON string provided */
    public void setTweetItems(String jsonLine) {
        if (jsonLine != null && !jsonLine.isEmpty()) {
            tweets.clear();
            JsonParser parser = new JsonParser(jsonLine);
            JSONArray statuses = parser.getArray("statuses");
            parser.setArray(statuses);
            for (int i = 0; i < statuses.length(); i++) {
                try {
                    tweets.add(new Tweet(parser.getObject(i)));
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            refresh();
        }
    }

    /** Set the timeline with the JSON string provided */
    public void setTimeline(String timeline) {
        if (timeline != null && !timeline.isEmpty()) {
            tweets.clear();
            JsonParser parser = new JsonParser(timeline);
            if (parser.getParentArray() != null) {
                for (int i = 0; i < parser.getParentArray().length(); i++) {
                    try {
                        tweets.add(new Tweet(parser.getObject(i)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            /*JsonParser profileParser;
            try {
                profileParser = new JsonParser((JSONObject) parser.getParentArray().get(0));
                profile = new Profile(profileParser.getObject("user"), this);
                profile.addObserver(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            refresh();
        }
    }

    /** Set the list with user id's to match the JSON result String */
    public void setFriends(String result) {
        JsonParser parser = new JsonParser(result);
        if (result != null) {
            JSONArray idArray = parser.getArray("ids");
            for (int i = 0; i < idArray.length(); i++) {
                try {
                    if (!friendIds.contains(idArray.get(i))) {
                        friendIds.add(idArray.getString(i));
                        Log.w("testing", "Added to friends id: " + idArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** Set the list of friend names with the given json String */
    public void setFriendNames(String result) {
        friendNames.clear();
        JsonParser parser = new JsonParser(result);
        if (result != null) {
            JSONArray userArray = parser.getArray("users");
            for (int i = 0; i < userArray.length(); i++) {
                try {
                    JSONObject user = (JSONObject) userArray.get(i);
                    if (!friendNames.contains(user.getString("screen_name"))) {
                        friendNames.add(user.getString("screen_name"));
                        Log.w("testing", "Added to friend names: " + user.getString("screen_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        setChanged();
        notifyObservers();
    }

    /** Set the list of follower names with the given json String */
    public void setFollowerNames(String result) {
        followerNames.clear();
        JsonParser parser = new JsonParser(result);
        if (result != null) {
            JSONArray userArray = parser.getArray("users");
            for (int i = 0; i < userArray.length(); i++) {
                try {
                    JSONObject user = (JSONObject) userArray.get(i);
                    if (!followerNames.contains(user.getString("screen_name"))) {
                        followerNames.add(user.getString("screen_name"));
                        Log.w("testing", "Added to follower names: " + user.getString("screen_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        setChanged();
        notifyObservers();
    }

    /**********************************************************************************/
    /**************************** MISCELLANEOUS METHODS *******************************/
    /**********************************************************************************/

    /** Checks if this model already contains a instance of the user with the given id_str */
    public User userExists(String id_str) {
        for (User u : users) {
            if (u.getId().equals(id_str)) {
                return u;
            }
        }
        return null;
    }

    /** Clear the saved tweets inside this model */
    public void clearTweets() {
        tweets.clear();
        refresh();
    }

    /** Notify the observers of this model that something has changed */
    public void refresh() {
        setChanged();
        notifyObservers();
    }

    /** Call refresh when something has changed */
    @Override
    public void update(Observable observable, Object data) {
        refresh();
    }
}