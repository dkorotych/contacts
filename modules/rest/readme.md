## Summary

This module provides REST services for working with contacts, that are stored in directory service.

All services require basic authentication.

## Framework

This module is based on [Spring][framework:spring] framework.

## Tools

To work on this project you can use: [Git][tool:git], [Maven][tool:maven], [Eclipse][tool:eclipse], [Tomcat][tool:tomcat] and [Open DJ][tool:opendj].

## Getting Started

1. Install and configure local directory service (for example, [Open DJ][tool:opendj]). File [test.ldif](https://github.com/grytsenko/contacts/blob/master/modules/rest/config/test.ldif) contains test data for directory service.
1. Build module and deploy it on web server.
1. Open `http://localhost:8080/contacts/search.json` in browser and enter user name and password for authentication.

[framework:spring]: http://www.springsource.org/

[tool:git]: http://git-scm.com/
[tool:maven]: http://maven.apache.org/
[tool:tomcat]: http://tomcat.apache.org/
[tool:eclipse]: http://www.eclipse.org/
[tool:opendj]: http://forgerock.com/what-we-offer/open-identity-stack/opendj/
