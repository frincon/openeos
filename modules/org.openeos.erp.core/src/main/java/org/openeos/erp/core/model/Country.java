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
 * Country generated by hbm2java
 */
@Entity
@Table(name="C_COUNTRY"
    , uniqueConstraints = @UniqueConstraint(columnNames="COUNTRYCODE") 
)
public class Country  implements java.io.Serializable {


     public static final String PROPERTY_ID = "id";
     public static final String PROPERTY_NAME = "name";
     public static final String PROPERTY_DESCRIPTION = "description";
     public static final String PROPERTY_COUNTRYCODE = "countrycode";
     public static final String PROPERTY_REGION_DEFINED = "regionDefined";
     public static final String PROPERTY_REGIONNAME = "regionname";
     public static final String PROPERTY_POSTAL_ADD = "postalAdd";
     public static final String PROPERTY_AD_LANGUAGE = "adLanguage";
     public static final String PROPERTY_REGIONS = "regions";
     public static final String PROPERTY_LOCATIONS = "locations";

     private String id;
     private String name;
     private String description;
     private String countrycode;
     private boolean regionDefined;
     private String regionname;
     private boolean postalAdd;
     private String adLanguage;
     private Set<Region> regions = new HashSet<Region>(0);
     private Set<Location> locations = new HashSet<Location>(0);

    public Country() {
    }

	
    public Country(String name, String countrycode, boolean regionDefined, boolean postalAdd) {
        this.name = name;
        this.countrycode = countrycode;
        this.regionDefined = regionDefined;
        this.postalAdd = postalAdd;
    }
    public Country(String name, String description, String countrycode, boolean regionDefined, String regionname, boolean postalAdd, String adLanguage, Set<Region> regions, Set<Location> locations) {
       this.name = name;
       this.description = description;
       this.countrycode = countrycode;
       this.regionDefined = regionDefined;
       this.regionname = regionname;
       this.postalAdd = postalAdd;
       this.adLanguage = adLanguage;
       this.regions = regions;
       this.locations = locations;
    }
   
     @GenericGenerator(name="generator", strategy="uuid")@Id @GeneratedValue(generator="generator")

    
    @Column(name="C_COUNTRY_ID", unique=true, nullable=false, length=32)
    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
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

    
    @Column(name="COUNTRYCODE", unique=true, nullable=false, length=2)
    public String getCountrycode() {
        return this.countrycode;
    }
    
    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    
    @Column(name="HASREGION", nullable=false)
    public boolean isRegionDefined() {
        return this.regionDefined;
    }
    
    public void setRegionDefined(boolean regionDefined) {
        this.regionDefined = regionDefined;
    }

    
    @Column(name="REGIONNAME", length=60)
    public String getRegionname() {
        return this.regionname;
    }
    
    public void setRegionname(String regionname) {
        this.regionname = regionname;
    }

    
    @Column(name="HASPOSTAL_ADD", nullable=false)
    public boolean isPostalAdd() {
        return this.postalAdd;
    }
    
    public void setPostalAdd(boolean postalAdd) {
        this.postalAdd = postalAdd;
    }

    
    @Column(name="AD_LANGUAGE", length=6)
    public String getAdLanguage() {
        return this.adLanguage;
    }
    
    public void setAdLanguage(String adLanguage) {
        this.adLanguage = adLanguage;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="country")
    public Set<Region> getRegions() {
        return this.regions;
    }
    
    public void setRegions(Set<Region> regions) {
        this.regions = regions;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="country")
    public Set<Location> getLocations() {
        return this.locations;
    }
    
    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }




}


