FROM openjdk:21

ARG JAR_FILE=./build/libs/이름-버전.jar

COPY ${JAR_FILE} 이름.jar

ENTRYPOINT ["java","-jar","/이름.jar"]
