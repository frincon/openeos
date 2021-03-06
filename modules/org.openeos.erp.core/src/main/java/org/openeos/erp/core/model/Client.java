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
package org.openeos.erp.core.model;
// Generated Apr 22, 2014 5:16:14 PM by Hibernate Tools 4.0.0
// Template generated from org.openeos.hibernate.hbm2java


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

/**
 * Client generated by hbm2java
 */
@Entity
@Table(name="AD_CLIENT"
    , uniqueConstraints = {@UniqueConstraint(columnNames="NAME"), @UniqueConstraint(columnNames="VALUE")} 
)
public class Client  implements java.io.Serializable {


     public static final String PROPERTY_ID = "id";
     public static final String PROPERTY_VALUE = "value";
     public static final String PROPERTY_NAME = "name";
     public static final String PROPERTY_DESCRIPTION = "description";
     public static final String PROPERTY_ORGANIZATIONS = "organizations";

     private String id;
     private String value;
     private String name;
     private String description;
     private Set<Organization> organizations = new HashSet<Organization>(0);

    public Client() {
    }

	
    public Client(String value, String name) {
        this.value = value;
        this.name = name;
    }
    public Client(String value, String name, String description, Set<Organization> organizations) {
       this.value = value;
       this.name = name;
       this.description = description;
       this.organizations = organizations;
    }
   
     @GenericGenerator(name="generator", strategy="uuid")@Id @GeneratedValue(generator="generator")

    
    @Column(name="AD_CLIENT_ID", unique=true, nullable=false, length=32)
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    
    @Column(name="VALUE", unique=true, nullable=false, length=40)
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    
    @Column(name="NAME", unique=true, nullable=false, length=60)
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

@OneToMany(fetch=FetchType.LAZY, mappedBy="client")
    public Set<Organization> getOrganizations() {
        return this.organizations;
    }
    
    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }




}


