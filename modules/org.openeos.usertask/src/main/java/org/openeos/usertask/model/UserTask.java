/**
 * Copyright 2014 Fernando Rincon Martin <frm.rincon@gmail.com>
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
package org.openeos.usertask.model;
// Generated Apr 4, 2014 9:47:47 PM by Hibernate Tools 4.0.0
// Template generated from org.openeos.hibernate.hbm2java


import org.openeos.erp.core.model.User;
import org.openeos.usertask.model.list.TaskStatus;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * UserTask generated by hbm2java
 */
@Entity
@Table(name="UT_USERTASK"
)
public class UserTask  implements java.io.Serializable {


     public static final String PROPERTY_ID = "id";
     public static final String PROPERTY_USER = "user";
     public static final String PROPERTY_NAME = "name";
     public static final String PROPERTY_DESCRIPTION = "description";
     public static final String PROPERTY_PRIORITY = "priority";
     public static final String PROPERTY_STATUS = "status";
     public static final String PROPERTY_META_DATA = "metaData";

     private String id;
     private User user;
     private String name;
     private String description;
     private int priority;
     private TaskStatus status;
     private Set<UserTaskMetaData> metaData = new HashSet<UserTaskMetaData>(0);

    public UserTask() {
    }

	
    public UserTask(String name, int priority, TaskStatus status) {
        this.name = name;
        this.priority = priority;
        this.status = status;
    }
    public UserTask(User user, String name, String description, int priority, TaskStatus status, Set<UserTaskMetaData> metaData) {
       this.user = user;
       this.name = name;
       this.description = description;
       this.priority = priority;
       this.status = status;
       this.metaData = metaData;
    }
   
     @GenericGenerator(name="generator", strategy="uuid")@Id @GeneratedValue(generator="generator")

    
    @Column(name="UT_USERTASK_ID", unique=true, nullable=false, length=32)
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ASSIGNED_USER_ID")
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    
    @Column(name="NAME", nullable=false, length=60)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="DESCRIPTION")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    
    @Column(name="PRIORITY", nullable=false)
    public int getPriority() {
        return this.priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }

    
    @Column(name="STATUS", nullable=false, length=60)
    public TaskStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="userTask")
    public Set<UserTaskMetaData> getMetaData() {
        return this.metaData;
    }
    
    public void setMetaData(Set<UserTaskMetaData> metaData) {
        this.metaData = metaData;
    }




}


