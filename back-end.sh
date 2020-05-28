
rm -rf build/*
mkdir build/WEB-INF
mkdir build/WEB-INF/classes
find src -name *.java >javaFiles.txt
javac -d build/WEB-INF/classes -cp .:./WebContent/WEB-INF/lib/*:/usr/local/apache-tomcat-7.0.103/lib/servlet-api.jar:/usr/local/apache-tomcat-7.0.103/lib/jsp-api.jar  @javaFiles.txt
cp -r src/* build/WEB-INF/classes
#rm -rf build/WEB-INF/classes/*.java
find build/WEB-INF/classes -name "*.java" -exec rm -rf {} \;
cp -r WebContent/* build
jar -cvf RapidFeedback2_war.war  -C build/ . 
