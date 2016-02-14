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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JiraIssue {
    private final String title;
    private final String labels;
    private final String priority;
    private final String issueType;
    private final String projectKey;
    private final String components;
    private final String description;

    public JiraIssue(final String projectKey, final String labels, final String issueType, final String components, final String priority, final String title, final String description) {
        this.title = title;
        this.labels = labels;
        this.priority = priority;
        this.issueType = issueType;
        this.projectKey = projectKey;
        this.components = components;
        this.description = description;
    }

    public String toJSONString() {
        JSONObject project = new JSONObject();
        project.put("key", projectKey);

        JSONObject issuetype = new JSONObject();
        issuetype.put("name", issueType);

        JSONObject level = new JSONObject();
        level.put("name", priority);

        JSONArray labelList = new JSONArray();
        String[] labelArr = labels.split("\\,");
        for (String s : labelArr) {
            labelList.add(s);
        }

        JSONArray componentList = new JSONArray();
        String[] compArr = components.split("\\,");
        for (String s : compArr) {
            JSONObject hash = new JSONObject();
            hash.put("name", s);
            componentList.add(hash);
        }

        JSONObject obj = new JSONObject();
        obj.put("summary", title);
        obj.put("description", description);
        obj.put("project", project);
        obj.put("issuetype", issuetype);
        obj.put("labels", labelList);
        obj.put("components", componentList);
        obj.put("priority", level);

        JSONObject fields = new JSONObject();
        fields.put("fields", obj);

        return fields.toJSONString();
    }

}
