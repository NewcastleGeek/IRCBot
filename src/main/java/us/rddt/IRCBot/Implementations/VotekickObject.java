package us.rddt.IRCBot.Implementations;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.User;

/**
 * A simple class to define specific instances of votekicks.
 * 
 * @see us.rddt.IRCBot.Handlers.Votekick
 * @author Ryan Morrison
 */
public class VotekickObject {
    // Class variables
    private User user;
    private int numVotes;
    private int numVotesRequired;
    private List<String> votedUsers = new ArrayList<String>();
    
    /**
     * Class constructor
     * @param user the user being votekicked
     * @param startingUser the user who started the votekick
     * @param numVotesRequired the number of votes required for the vote to pass
     */
    public VotekickObject(User user, User startingUser, int numVotesRequired) {
        this.user = user;
        this.numVotes = 1;
        this.numVotesRequired = numVotesRequired;
        votedUsers.add(startingUser.getHostmask());
    }
    
    /**
     * Adds a vote against the user
     */
    public void addVote() {
        numVotes++;
    }
    
    /**
     * Adds the user who voted to the voted users' array
     * @param user the user to add
     */
    public void addVotedUser(User user) {
        votedUsers.add(user.getHostmask());
    }

    /**
     * Returns the number of current votes against the user
     * @return the number of current votes against the user
     */
    public int getNumVotes() {
        return numVotes;
    }

    /**
     * Returns the number of votes required to kick
     * @return the number of votes required to kick
     */
    public int getNumVotesRequired() {
        return numVotesRequired;
    }
    
    /**
     * Returns the users who voted in the votekick
     * @return the users who voted in the votekick
     */
    public List<String> getVotedUsers() {
        return votedUsers;
    }
    
    /**
     * Returns the User object
     * @return the User object
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Returns if there are enough votes for the votekick to pass
     * @return true if the votekick has passed, false if it has not
     */
    public boolean hasNeededVotes() {
        return numVotes >= numVotesRequired;
    }
}
