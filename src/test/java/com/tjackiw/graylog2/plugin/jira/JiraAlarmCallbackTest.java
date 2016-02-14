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

import com.tjackiw.graylog2.plugin.jira.callback.JiraAlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class JiraAlarmCallbackTest {
    private JiraAlarmCallback alarmCallback;
    private Configuration configuration;

    @Before
    public void setUp() {
        alarmCallback = new JiraAlarmCallback();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("project_key", "TEST");
        config.put("labels", "graylog,alert");
        config.put("issue_type", "Bug");
        config.put("components", "Alert");
        config.put("instance_url", "http://example.com");
        config.put("graylog_url", "http://example.com");
        config.put("username", "foo");
        config.put("password", "bar");
        config.put("priority", "high");
        configuration = new Configuration(config);
    }

    @Test
    public void testInitialize() throws AlarmCallbackConfigurationException {
        alarmCallback.initialize(configuration);
    }

    @Test
    public void testGetAttributes() throws AlarmCallbackConfigurationException {
        alarmCallback.initialize(configuration);

        final Map<String, Object> attributes = alarmCallback.getAttributes();
        assertThat(attributes.keySet(), hasItems("project_key", "labels", "issue_type", "components", "instance_url", "username", "password", "graylog_url", "priority"));
        assertThat((String) attributes.get("password"), equalTo("****"));
    }

    @Test
    public void checkConfigurationSucceedsWithValidConfiguration() throws AlarmCallbackConfigurationException, ConfigurationException {
        alarmCallback.initialize(configuration);
        alarmCallback.checkConfiguration();
    }

    @Test(expected = AlarmCallbackConfigurationException.class)
    public void checkConfigurationFailsIfInstanceUrlIsMissing() throws AlarmCallbackConfigurationException, ConfigurationException {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("project_key", "TEST");
        alarmCallback.initialize(new Configuration(config));
        alarmCallback.checkConfiguration();
    }

    @Test
    public void testGetRequestedConfiguration() {
        assertThat(alarmCallback.getRequestedConfiguration().asList().keySet(),
                hasItems("project_key", "labels", "issue_type", "components", "instance_url", "username", "password"));
    }

    @Test
    public void testGetName() {
        assertThat(alarmCallback.getName(), equalTo("JIRA Alarm Callback"));
    }
}
