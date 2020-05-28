sudo su

rm -rf build/*
mkdir build/WEB-INF
mkdir build/WEB-INF/classes
find src -name *.java >javaFiles.txt
javac -d build/WEB-INF/classes -cp .:./WebContent/WEB-INF/lib/*:/usr/local/apache-tomcat-7.0.104/lib/servlet-api.jar:/usr/local/apache-tomcat-7.0.104/lib/jsp-api.jar  @javaFiles.txt
cp -r src/* build/WEB-INF/classes
#rm -rf build/WEB-INF/classes/*.java
find build/WEB-INF/classes -name "*.java" -exec rm -rf {} \;
cp -r WebContent/* build
jar -cvf RapidFeedback2_war.war  -C build/ . 

cp RapidFeedback2_war.war /usr/local/apache-tomcat-7.0.104/webapps/
cp rapid-feedback.html /usr/local/apache-tomcat-7.0.104/webapps/webpage/

cd /usr/local/apache-tomcat-7.0.104/webapps/

rm -rf RapidFeedback2_war

cd ..

./bin/shutdown.sh
./bin/startup.sh