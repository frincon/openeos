OpenEOS
============
[![Build Status](https://travis-ci.org/frincon/openeos.png?branch=master)](https://travis-ci.org/frincon/openeos)

OpenEOS aims to be an Enterprise Operating System. OpenEOS is a project that tries to bring together all Open Source technologies involved to make a modular applications for Business like ERP or CRM. Simultaniously, the projects wants to make an Open Source ERP to be used by small, medium and large companies.

Architecture
------------

The project is based mainly in OSGi framework and all the components in the projects are OSGi bundles. The components has been built with abstraction in mind and it can be ease replaced by other modules with same functionality.

At the moment, the framework has integrated this technologies:
- jBPM
- Hibernate
- Liquibase (with extender pattrern to update database)
- AbstractForm library to make forms independent of GUI library
- Vaadin GUI implementation
- Console GUI implementation that can be accessed by SSH protocol based on Lanterna
- Abstract layer for reporting (Actually it can use JasperReports but in the future more frameworks can be integrated)
- Abstract layer for document management (Actually it use database BLOBs as a backend but its planned to use CMS Repositories like JCR or Alfresco)
- User Tasks Centric

The code uses AspectJ and OSGi Blueprint technologies.

ERP
---

For now there are two main ERP modules in development: Accounting and Sales.
