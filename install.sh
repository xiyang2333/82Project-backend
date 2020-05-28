sudo su

wget https://downloads.apache.org/tomcat/tomcat-7/v7.0.104/bin/apache-tomcat-7.0.104.tar.gz

tar -zvxf apache-tomcat-7.0.104.tar.gz

mkdir apache-tomcat-7.0.104/webapps/downloads
mkdir apache-tomcat-7.0.104/webapps/webpage/

mv apache-tomcat-7.0.104 /usr/local/
