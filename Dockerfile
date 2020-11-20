FROM openjdk:8-jre-alpine
ENV APP_FILE app.war
ENV APP_HOME /usr/apps
EXPOSE 8080
COPY target/*.war $APP_HOME/$APP_FILE
WORKDIR $APP_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec java -jar $APP_FILE"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.war"]
