# Update Alfresco from Community - 5.1.0 (r127059-b7) to alfresco-content-services-community-distribution-7.2.1
[Follow this](https://docs.alfresco.com/content-services/community/upgrade/)

## Stop service
sudo systemctl stop alfresco

## Backup alfresco
tar -czvf alfresco-backup.tar.gz /usr/local/alfresco-community

## Backup db
mysqldump -uspisal -pSp1ssa --host=localhost --port=3306 prodCopy -r prodCopy.sql
tar -czvf prodCopy.tar.gz prodCopy.sql
rm -rf prodCopy.sql

mysqldump -uspisal -pSp1ssa --host=localhost --port=3306 alfresco -r alfresco.sql
tar -czvf alfresco.tar.gz alfresco.sql
rm -rf alfresco.sql

## Import alfresco db as alfresco_7_2
mysql -u root -p
CREATE DATABASE alfresco_7_2;
CREATE USER 'alfresco_7_2'@'localhost' IDENTIFIED BY 'alfresco_7_2';
CREATE USER 'alfresco_7_2'@'%' IDENTIFIED BY 'alfresco_7_2';
USE alfresco_7_2;
GRANT ALL PRIVILEGES ON alfresco_7_2.* TO 'alfresco_7_2'@'localhost';
GRANT ALL PRIVILEGES ON alfresco_7_2.* TO 'alfresco_7_2'@'%';
exit
mysql --protocol=tcp --host=localhost --user=alfresco_7_2 --password=alfresco_7_2 --port=3306 --default-character-set=utf8 --database=alfresco_7_2 < alfresco.sql

## Install Java 11
https://www.oracle.com/java/technologies/downloads/#java11
scp -i id_rsa .\jdk-11.0.16_linux-x64_bin.tar.gz root@10.172.44.105:/usr/local
tar -xzf jdk-11.0.16_linux-x64_bin.tar.gz
export JAVA_HOME=/usr/local/jdk-11.0.16


## Install Alfresco 7.2
Copy installed version from: sftp://root@10.172.44.105//usr/local/alfresco-community-7.2.1, which contains googledocs integration AMP, mysql drivers and is patched for cors.

Or download from https://artifacts.alfresco.com/nexus/#nexus-search;gav~org.alfresco~alfresco-content-services-community-distribution~~~~kw,versionexpand
and follow guide to install google docs amps, cors and mysql driver.

## Configuration
All config in: sftp://root@10.172.44.105//usr/local/alfresco-community-7.2.1/tomcat/bin/setenv.sh

###Datasource
Inside alfresco-community-7.2.1/tomcat/bin/setenv.sh, change:
 -Ddir.root=${ALFRESCO_HOME}/alf_data
 -Ddb.username=alfresco_7_2
 -Ddb.password=alfresco_7_2
 -Ddb.schema.update=true
 -Ddb.driver=com.mysql.cj.jdbc.Driver
 -Ddb.url=\"jdbc:mysql://10.172.44.105/alfresco_7_2?useUnicode=yes&characterEncoding=UTF-8\"

## Copy alfresco data
cp -r /usr/local/alfresco-community/alf_data/. /usr/local/alfresco-community-7.2/alf_data

## Start alfresco
/usr/local/alfresco-community-7.2.1/tomcat/bin/startup.sh

## Logs
tail /usr/local/alfresco-community-7.2.1/tomcat/logs/catalina.out -f -n 1000

