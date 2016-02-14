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

package com.tjackiw.graylog2.plugin.jira.callback;

import com.google.common.collect.Maps;
import com.tjackiw.graylog2.plugin.jira.JiraClient;
import com.tjackiw.graylog2.plugin.jira.JiraIssue;
import com.tjackiw.graylog2.plugin.jira.JiraPluginBase;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.streams.Stream;

import java.util.Map;

public class JiraAlarmCallback extends JiraPluginBase implements AlarmCallback {
    private Configuration configuration;

    @Override
    public void initialize(final Configuration config) throws AlarmCallbackConfigurationException {
        this.configuration = config;
        try {
            checkConfiguration(config);
        } catch (ConfigurationException e) {
            throw new AlarmCallbackConfigurationException("Configuration error. " + e.getMessage());
        }
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException {
        final JiraClient client = new JiraClient(
                configuration.getString(CK_INSTANCE_URL),
                configuration.getString(CK_USERNAME),
                configuration.getString(CK_PASSWORD)
        );

        JiraIssue jiraIssue = new JiraIssue(
                configuration.getString(CK_PROJECT_KEY),
                configuration.getString(CK_LABELS),
                configuration.getString(CK_ISSUE_TYPE),
                configuration.getString(CK_COMPONENTS),
                configuration.getString(CK_PRIORITY),
                buildTitle(stream),
                buildDescription(stream, result, configuration)
        );
        try {
            client.send(jiraIssue);
        } catch (JiraClient.JiraClientException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Maps.transformEntries(configuration.getSource(), new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(String key, Object value) {
                if (CK_PASSWORD.equals(key)) {
                    return "****";
                }
                return value;
            }
        });
    }

    @Override
    // Never actually called by graylog-server
    public void checkConfiguration() throws ConfigurationException {
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        return configuration();
    }

    @Override
    public String getName() {
        return "JIRA Alarm Callback";
    }
}
