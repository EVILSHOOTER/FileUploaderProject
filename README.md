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
- JUnit 4.13.2
- HTML, CSS, JavaScript

## How to setup and use
- Click the green "Code" button and download the project using "Download ZIP".
- Extract the project folder into your Eclipse working directory
- Open the Eclipse IDE and import the project (File > Import > General > Existing Projects Into Workspace) 

## Features
- Public downloads list page with search function (by download or user name)
- Individual download pages with metadata (download counters, time/date, username, virus scans
- Edit functionality of a download's details
- Users and verified status
- Upload timers and size limits (lower for non-users)

## Decisions and tradeoffs that were made (due to time)
- Use of the official virus scan API provided by VirusTotal, and instead using a simpler service. (free-tier only allowed one upload per minute)
- More test cases focusing on:
  - downloads/uploads being terminated mid-session
  - comparing stored hash keys to downloaded files 

## Improvements/changes that can be made
- Create a seperate download and upload controller class that would deliver custom progress bars with statistics and resumable sessions
- Improve the search functionality to arrange via:
  - username
  - download count (for each download)
- Version control for individual downloads
- More presentable CSS potentially using a framework like Bootstrap (only vanilla was used in this case)
- Comment sections and like/dislike ratios for individual downloads
- User trust ratings
- Email verification upon account creation
