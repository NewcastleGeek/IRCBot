/*
 * This file is part of IRCBot.
 * Copyright (c) 2011-2013 Ryan Morrison
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;

/**
 * Uses a Google API to perform conversions provided by users via a bot command.
 * 
 * @see us.rddt.IRCBot.Handlers.Convert
 * @author Ryan Morrison
 */
public class Converter {
    /*
     * Variables
     */
    private String lhs;
    private String rhs;
    private String error;
    private boolean icc;
    
    /**
     * Class constructor
     */
    public Converter() {  
    }
    
    /**
     * Class constructor
     * @param lhs the left hand side of the equation (input)
     * @param rhs the right hand side of the equation (output)
     * @param error the error string (if applicable)
     * @param icc
     */
    public Converter(String lhs, String rhs, String error, boolean icc) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.error = error;
        this.icc = icc;
    }
    
    /**
     * Gets information about a provided link to a Reddit user page.
     * @param link the link to the user page
     * @return a new instance of the class with the user's details
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static Converter convert(String lhs) throws IOException, JSONException {
        /*
         * Variables
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;
        URL link = new URL("http://www.google.com/ig/calculator?hl=en&q=" + lhs.replace(" ", "%20"));

        /*
         * Opens a connection to the Google API, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)link.openConnection();
        conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
        if(conn.getResponseCode() >= 400) {
            throw new IOException("Server returned response code: " + conn.getResponseCode());
        }

        BufferedReader buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((buffer = buf.readLine()) != null) {
            jsonToParse.append(buffer);
        }

        /*
         * Disconnect from the server.
         */
        conn.disconnect();

        /*
         * Parse the JSON data.
         */
        JSONObject result = new JSONObject(jsonToParse.toString());
        return new Converter(result.getString("lhs"), result.getString("rhs"), result.getString("error"), result.getBoolean("icc"));
    }

    /**
     * Returns the left hand side of the equation (input)
     * @return the left hand side of the equation (input)
     */
    public String getLhs() {
        return lhs;
    }

    /**
     * Returns the right hand side of the equation (output)
     * @return the right hand side of the equation (output)
     */
    public String getRhs() {
        return rhs;
    }
    
    /**
     * Returns the error string (if applicable)
     * @return the error string (if applicable)
     */
    public String getError() {
        return error;
    }

    /**
     * Returns the value of icc
     * @return the value of icc
     */
    public boolean isIcc() {
        return icc;
    }
}
