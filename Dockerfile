# Problema 1: Usando imagem base muito pesada
FROM openjdk:17-jdk-slim

# Problema 2: Rodando como root
WORKDIR /app

# Problema 3: Copiando arquivos desnecessários
COPY . .

# Problema 4: Build dentro do container (lento)
RUN ./gradlew build -x test

# Problema 5: Expondo informações de build
RUN ls -la build/libs/

# Problema 6: Comando hardcoded
CMD ["java", "-jar", "build/libs/payment-api-0.0.1-all.jar"]

EXPOSE 8080
