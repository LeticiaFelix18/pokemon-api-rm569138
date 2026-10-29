FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY . ./
RUN chmod +x mvnw
RUN ./mvnw -B -DskipTests clean package
ENV TZ=America/Sao_Paulo
EXPOSE 8080
CMD ["java", "-Duser.timezone=America/Sao_Paulo", "-jar", "target/pokemon-0.0.1-SNAPSHOT.jar"]
