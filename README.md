# NOTE: This project is no longer maintened.

JIRA Plugin for Graylog
==========================

[![Build Status](https://travis-ci.org/tjackiw/graylog-plugin-jira.svg)](https://travis-ci.org/tjackiw/graylog-plugin-jira)

An alarm callback plugin that integrates [JIRA](https://www.atlassian.com/software/jira/) into [Graylog](https://www.graylog.org/).

![](https://github.com/tjackiw/graylog-plugin-jira/blob/master/screenshot-jira.png)

**Required Graylog version:** 1.0 and later

## Installation

[Download the plugin](https://github.com/tjackiw/graylog-plugin-jira/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

## Usage

Create a "JIRA Alarm Callback" on the "Manage alerts" page of your stream. 
Enter the requested configuration and save. 
Make sure you also configured alert conditions for the stream so that the alerts are actually triggered.

![](https://github.com/tjackiw/graylog-plugin-jira/blob/master/screenshot-alert.png)

## Build

This project is using Maven and requires Java 7 or higher.

You can build a plugin (JAR) with `mvn package`.

## Copyright

Copyright (c) 2016 Thiago Jackiw. See LICENSE for further details.
