# fileUpload Website
A university project based around a file-sharing website, with a focus on custom download pages that can information such as descriptions, pre-generated hash keys, virus scan reports, and more.

## Code structure
The code is organised in an MVC (Model-View-Controller) structure with the help of DAOs (Data Access Objects)
- Model classes can be found in the "model" folder, [src/main/java/net/aqdas/server/model](https://github.com/EVILSHOOTER/FileUploaderProject/tree/main/FileUploaderProject/src/main/java/net/aqdas/server/model)
- View classes are located in a "views" folder, separately in the "SUB-INF" subdirectory, [src/main/webapp/WEB-INF/views/](https://github.com/EVILSHOOTER/FileUploaderProject/tree/main/FileUploaderProject/src/main/webapp/WEB-INF/views)
- Controller classes are located in the "controller" folder, [src/main/java/net/aqdas/server/controller](https://github.com/EVILSHOOTER/FileUploaderProject/tree/main/FileUploaderProject/src/main/java/net/aqdas/server/controller)
- DAOs can be found in the "dao" folder, [src/main/java/net/aqdas/server/dao](https://github.com/EVILSHOOTER/FileUploaderProject/tree/main/FileUploaderProject/src/main/java/net/aqdas/server/dao)

## Tools/Languages/Libraries used
- Java (with Eclipse IDE for Enterprise Java Developers 9.1)
- MySQL (with MySQL Workbench 8.0.22)
- JSPs (Java/Jakarta Server Pages) with JSTL (Java/Jakarta Standard Tag Library)
- HTML, CSS, JavaScript

## How to setup and use

## Decisions and tradeoffs that were made (due to time)
- 

## Improvements/changes that could have been made
- Create a seperate download and upload controller class that would deliver custom  
