/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.model;

import java.io.Serializable;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RuntimeLogSummary implements Serializable {

  private long id;
  private String time;
  private String logLine;
  private String type;

  public RuntimeLogSummary() {
  }

  public RuntimeLogSummary(long id, String time, String logLine, String type) {
    this.id = id;
    this.time = time;
    this.logLine = logLine;
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getLogLine() {
    return logLine;
  }

  public void setLogLine(String logLine) {
    this.logLine = logLine;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
