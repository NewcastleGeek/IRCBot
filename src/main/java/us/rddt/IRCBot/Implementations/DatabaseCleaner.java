/*
 * This file is part of IRCBot.
 * Copyright (c) 2011 Ryan Morrison
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions, and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions, and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author of this software nor the name of
 *  contributors to this software may be used to endorse or promote products
 *  derived from this software without specific prior written consent.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */

package us.rddt.IRCBot.Implementations;

import java.sql.PreparedStatement;
import java.util.logging.Level;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.Database;
import us.rddt.IRCBot.IRCUtils;

/**
 * Cleans the database, removing old and unnecessary entries after a specified period of time.
 * @author Ryan Morrison
 */
public class DatabaseCleaner implements Runnable {
    public void run() {
        // Variables
        Database database;
        PreparedStatement statement;
        int status;

        try {
            // Connect to the database
            database = new Database();
            database.connect();

            // Clean out deleted quotes older than one week
            statement = database.getConnection().prepareStatement("DELETE FROM Quotes WHERE Deleted = '1' AND Date < DATE_SUB(NOW(), INTERVAL 7 DAY)");
            status = statement.executeUpdate();
            Configuration.getLogger().write(Level.INFO, "Cleaned up " + status + " deleted quotes from the database.");

            statement.close();

            // Clean out game statuses older than 12 hours
            statement = database.getConnection().prepareStatement("DELETE FROM GameStatus WHERE Date < DATE_SUB(NOW(), INTERVAL 12 HOUR)");
            status = statement.executeUpdate();
            Configuration.getLogger().write(Level.INFO, "Cleaned up " + status + " stale game statuses from the database.");

            database.disconnect();
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        }
    }
}
