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


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

/**
 * UserTaskMetaData generated by hbm2java
 */
@Entity
@Table(name="UT_USERTASK_METADATA"
    , uniqueConstraints = @UniqueConstraint(columnNames={"UT_USERTASK_ID", "META_KEY"}) 
)
public class UserTaskMetaData  implements java.io.Serializable {


     public static final String PROPERTY_ID = "id";
     public static final String PROPERTY_USER_TASK = "userTask";
     public static final String PROPERTY_META_KEY = "metaKey";
     public static final String PROPERTY_VALUE = "value";

     private String id;
     private UserTask userTask;
     private String metaKey;
     private String value;

    public UserTaskMetaData() {
    }

	
    public UserTaskMetaData(UserTask userTask, String metaKey) {
        this.userTask = userTask;
        this.metaKey = metaKey;
    }
    public UserTaskMetaData(UserTask userTask, String metaKey, String value) {
       this.userTask = userTask;
       this.metaKey = metaKey;
       this.value = value;
    }
   
     @GenericGenerator(name="generator", strategy="uuid")@Id @GeneratedValue(generator="generator")

    
    @Column(name="UT_USERTASK_METADATA_ID", unique=true, nullable=false, length=32)
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="UT_USERTASK_ID", nullable=false)
    public UserTask getUserTask() {
        return this.userTask;
    }
    
    public void setUserTask(UserTask userTask) {
        this.userTask = userTask;
    }

    
    @Column(name="META_KEY", nullable=false, length=2000)
    public String getMetaKey() {
        return this.metaKey;
    }
    
    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    
    @Column(name="VALUE", length=32700)
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }




}

