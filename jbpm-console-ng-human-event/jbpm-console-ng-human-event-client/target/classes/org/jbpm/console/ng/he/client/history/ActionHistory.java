/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.he.client.history;

import java.util.Queue;

import org.jbpm.console.ng.he.model.HumanEventSummary;

//@SessionScoped
public class ActionHistory {
    
    private Queue<HumanEventSummary> points;

	public Queue<HumanEventSummary> getPoints() {
		return points;
	}

	public void setPoints(Queue<HumanEventSummary> points) {
		this.points = points;
	}
    
//	private QueueSession points;
//
//	public QueueSession getPoints() {
//		return points;
//	}
//
//	public void setPoints(QueueSession points) {
//		this.points = points;
//	}

    

}
