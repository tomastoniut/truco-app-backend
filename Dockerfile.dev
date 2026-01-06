# Usamos imagen base con JDK 25 (Early Access o la más reciente disponible)
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

# Copiamos wrapper de maven y pom
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Copiamos el código fuente
COPY src ./src

# Comando de ejecución habilitando debug y devtools
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]