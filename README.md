# Converge

Converge is an editorial content management system for small and medium-size media houses who need to manage content for multiple outlets and re-publish it on multiple platforms (e.g. web sites, mobile apps, and newsletters).

Converge supports the following key processes:
Story writing - That is, the process of a writer securely typing a story directly in the system.

- Editorial review - That is, the process of passing a story through an editorial workflow where all changes are tracked.
- Newswire processing - That is, the process of creating a single view of newswires services where stories can be read and included in outlet editions.
- Forward planning - That is, the process of planning the content of an edition of an outlet. This inclues assigning stories to writers.

## Technology

Converge is a Java Enterprise Edition 5 (JEE5) applications and must be installed on a JEE6-compliant application server. Converge uses the Java Persistence API (JPA) and Java Database Connectivity (JDBC) to communicate with the database server and is therefore database independent. Converge has been extensively tested and used in production with the MySQL Database Server 5.5. User management is done through the Lightweight Directory Access Protocol (LDAP) such as OpenDS, OpenLDAP or Microsoft Active Directory. Converge operates on a full open source stack consisting of:

- Operating System: Ubuntu 12.04+
- Application Server: Glassfish v2.1 (Upgrade to Glassfish v4.0 on the Roadmap)
- Database Server: MySQL Server 5.5 
- Web Server: Apache 2+
- Java Virtual Machine: Oracle Java 1.7
- Directory Service: OpenDS 2+

Converge can also operate on proprietary platforms using Microsoft Windows Server, Oracle Database and Application Servers, and Microsoft Active Directory. 

## Installing Converge

You'll find the installation guide for Converge at [https://getconverge.atlassian.net/wiki/display/CON/Installation+Guide](https://getconverge.atlassian.net/wiki/display/CON/Installation+Guide)

## More Information

- Product Information: [http://www.getconverge.com](http://www.getconverge.com)
- Issue Tracking: [http://issues.getconverge.com](http://issues.getconverge.com)
- User Documentation: [http://docs.getconverge.com/user](http://docs.getconverge.com/user)
- Developer Documentation: [http://docs.getconverge.com/dev](http://docs.getconverge.com/dev)
- Continuous Integration: [http://ci.getconverge.com](http://ci.getconverge.com)
- Source Code Quality: [http://sonarqube.getconverge.com](http://sonarqube.getconverge.com)

## License 

    GNU GENERAL PUBLIC LICENSE Version 3, 29 June 2007
    Copyright (C) 2010 - 2014  Allan Lykke Christensen

    This program is free software: you can redistribute it and/or modify it 
    under the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option) 
    any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
    more details.

    You should have received a copy of the GNU General Public License along with
    this program.  If not, see <http://www.gnu.org/licenses/>.
