FROM eclipse-temurin:17-jdk-focal

# Instalar dependências necessárias (wget, ca-certificates e Maven)
RUN apt-get update && apt-get install -y \
    wget \
    ca-certificates \
    maven

WORKDIR /app

COPY . /app

# Usar Maven para empacotar a aplicação
RUN mvn clean package

EXPOSE 8080

CMD ["java", "-jar", "target/prototipo_ia-1.0.jar", "--server.port=8080"]

