#!/bin/bash
# Copyright 2014 Fernando Rincon Martin <frm.rincon@gmail.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

mvn install:install-file -Dfile=gemini-blueprint-1.0.2.RELEASE.pom -DpomFile=gemini-blueprint-1.0.2.RELEASE.pom
mvn install:install-file -Dfile=gemini-blueprint-core-1.0.2.RELEASE.jar -DpomFile=gemini-blueprint-core-1.0.2.RELEASE.pom -Dsources=gemini-blueprint-core-1.0.2.RELEASE-sources.jar
mvn install:install-file -Dfile=gemini-blueprint-extender-1.0.2.RELEASE.jar -DpomFile=gemini-blueprint-extender-1.0.2.RELEASE.pom -Dsources=gemini-blueprint-extender-1.0.2.RELEASE-sources.jar
mvn install:install-file -Dfile=gemini-blueprint-io-1.0.2.RELEASE.jar -DpomFile=gemini-blueprint-io-1.0.2.RELEASE.pom -Dsources=gemini-blueprint-io-1.0.2.RELEASE-sources.jar
mvn install:install-file -Dfile=gemini-blueprint-test-1.0.2.RELEASE.jar -DpomFile=gemini-blueprint-test-1.0.2.RELEASE.pom -Dsources=gemini-blueprint-test-1.0.2.RELEASE-sources.jar
mvn install:install-file -Dfile=org.eclipse.equinox.weaving.hook_1.0.200.I20130319-1000.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.equinox.weaving.hook -Dversion=1.0.200.I20130319-1000 -DgeneratePom=true -Dpackaging=jar -Dsources=org.eclipse.equinox.weaving.hook.source_1.0.200.I20130319-1000.jar
mvn install:install-file -Dfile=org.eclipse.equinox.weaving.aspectj_1.0.300.I20130319-1000.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.equinox.weaving.aspectj -Dversion=1.0.300.I20130319-1000 -DgeneratePom=true -Dpackaging=jar -Dsources=org.eclipse.equinox.weaving.aspectj.source_1.0.300.I20130319-1000.jar
mvn install:install-file -Dfile=org.eclipse.core.databinding_1.4.1.v20120521-2329.jar -Dsources=org.eclipse.core.databinding.source_1.4.1.v20120521-2329.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.core.databinding -Dversion=1.4.1.v20120521-2329 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.databinding.beans_1.2.200.v20120523-1955.jar -Dsources=org.eclipse.core.databinding.beans.source_1.2.200.v20120523-1955.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.core.databinding.beans -Dversion=1.2.200.v20120523-1955 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.databinding.observable_1.4.1.v20120521-2329.jar -Dsources=org.eclipse.core.databinding.observable.source_1.4.1.v20120521-2329.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.core.databinding.observable -Dversion=1.4.1.v20120521-2329 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.databinding.property_1.4.100.v20120523-1955.jar -Dsources=org.eclipse.core.databinding.property.source_1.4.100.v20120523-1955.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.core.databinding.property -Dversion=1.4.100.v20120523-1955 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=com.ibm.icu_4.4.2.v20110823.jar -Dsources=com.ibm.icu.source_4.4.2.v20110823.jar -DgroupId=org.openeos.external -DartifactId=com.ibm.icu -Dversion=4.4.2.v20110823 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.common_3.6.100.v20120522-1841.jar -Dsources=org.eclipse.equinox.common.source_3.6.100.v20120522-1841.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.equinox.common -Dversion=3.6.100.v20120522-1841 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.http.servlet_1.1.400.v20130418-1354.jar -Dsources=org.eclipse.equinox.http.servlet.source_1.1.400.v20130418-1354.jar -DgroupId=org.openeos.external -DartifactId=org.eclipse.equinox.http.servlet -Dversion=1.1.400.v20130418-1354 -DgeneratePom=true -Dpackaging=jar
mvn install:install-file -Dfile=javax.servlet_3.0.0.v201112011016.jar -Dsources=javax.servlet_3.0.0.v201112011016.jar -DgroupId=org.openeos.external -DartifactId=javax.servlet -Dversion=3.0.0.v201112011016 -DgeneratePom=true -Dpackaging=jar

cd hibernate-tools
mvn clean install -DskipTests=true
cd ../hibernate3-maven-plugin-3.0
mvn clean install -DskipTests=true
cd ../jbpm
mvn clean install -DskipTests=true
cd ..

