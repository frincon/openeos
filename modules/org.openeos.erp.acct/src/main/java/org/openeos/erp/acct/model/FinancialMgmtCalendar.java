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
package org.openeos.erp.acct.model;
// Generated May 18, 2014 10:29:15 AM by Hibernate Tools 4.0.0
// Template generated from org.openeos.hibernate.hbm2java


import org.openeos.erp.core.model.Client;
import org.openeos.erp.core.model.Organization;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.GenericGenerator;

/**
 * FinancialMgmtCalendar generated by hbm2java
 */
@Entity
@Table(name="C_CALENDAR"
    , uniqueConstraints = @UniqueConstraint(columnNames={"AD_CLIENT_ID", "NAME"}) 
)
public class FinancialMgmtCalendar  implements java.io.Serializable {


     public static final String PROPERTY_ID = "id";
     public static final String PROPERTY_ORGANIZATION = "organization";
     public static final String PROPERTY_CLIENT = "client";
     public static final String PROPERTY_NAME = "name";
     public static final String PROPERTY_DESCRIPTION = "description";
     public static final String PROPERTY_FINANCIAL_MGMT_YEARS = "financialMgmtYears";

     private String id;
     private Organization organization;
     private Client client;
     private String name;
     private String description;
     private Set<FinancialMgmtYear> financialMgmtYears = new HashSet<FinancialMgmtYear>(0);

    public FinancialMgmtCalendar() {
    }

	
    public FinancialMgmtCalendar(Organization organization, Client client, String name) {
        this.organization = organization;
        this.client = client;
        this.name = name;
    }
    public FinancialMgmtCalendar(Organization organization, Client client, String name, String description, Set<FinancialMgmtYear> financialMgmtYears) {
       this.organization = organization;
       this.client = client;
       this.name = name;
       this.description = description;
       this.financialMgmtYears = financialMgmtYears;
    }
   
     @GenericGenerator(name="generator", strategy="uuid")@Id @GeneratedValue(generator="generator")

    
    @Column(name="C_CALENDAR_ID", unique=true, nullable=false, length=32)
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="AD_ORG_ID", nullable=false)
    public Organization getOrganization() {
        return this.organization;
    }
    
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="AD_CLIENT_ID", nullable=false)
    public Client getClient() {
        return this.client;
    }
    
    public void setClient(Client client) {
        this.client = client;
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

@OneToMany(fetch=FetchType.LAZY, mappedBy="financialMgmtCalendar")
    public Set<FinancialMgmtYear> getFinancialMgmtYears() {
        return this.financialMgmtYears;
    }
    
    public void setFinancialMgmtYears(Set<FinancialMgmtYear> financialMgmtYears) {
        this.financialMgmtYears = financialMgmtYears;
    }




}


