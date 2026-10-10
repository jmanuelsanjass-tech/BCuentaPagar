# Etapa 1: Build con Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos pom.xml y descargamos dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con JDK 21
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java","-jar","app.jar"]
