/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thiago Jackiw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.tjackiw.graylog2.plugin.jira;

import org.graylog2.plugin.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JiraClient {
    private static final Logger LOG = LoggerFactory.getLogger(JiraClient.class);

    private final String username;
    private final String password;
    private final String instanceURL;

    public JiraClient(String instanceURL, String username, String password) {
        this.username = username;
        this.password = password;
        this.instanceURL = instanceURL;
    }

    public class JiraClientException extends Exception {
        public JiraClientException(String msg) {
            super(msg);
        }

        public JiraClientException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public void send(JiraIssue jiraIssue) throws JiraClientException {
        String jiraURL = instanceURL;
        if (!jiraURL.endsWith("/")) {
            jiraURL += "/";
        }

        final URL url;
        try {
            url = new URL(jiraURL + "rest/api/latest/issue");
        } catch (MalformedURLException e) {
            throw new JiraClientException("[JIRA] Error while constructing instance URL.", e);
        }

        final HttpURLConnection conn;
        try {
            String credentials = Tools.encodeBase64(username + ":" + password);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Authorization", "Basic " + credentials);
            conn.addRequestProperty("Content-Type", "application/json");
        } catch (IOException e) {
            throw new JiraClientException("[JIRA] Could not open connection to " + jiraURL + ": ", e);
        }

        try (final OutputStream outputStream = conn.getOutputStream()) {
            outputStream.write(jiraIssue.toJSONString().getBytes());
            outputStream.flush();

            switch (conn.getResponseCode()) {
                case 401:
                    throw new JiraClientException("[JIRA] Invalid credentials. Make sure your username/password are set properly.");
                case 201:
                    LOG.info("[JIRA] Issue has been successfully created.");
                    break;
                default:
                    throw new JiraClientException("[JIRA] Unexpected HTTP response status " + conn.getResponseCode());
            }

        } catch (IOException e) {
            throw new JiraClientException("[JIRA] Could not POST event to " + jiraURL + ": ", e);
        }
    }

}
