FROM openjdk:17
COPY build/libs/TradeServer-0.0.1.jar TradeServer.jar
ENTRYPOINT ["java", "-jar", "TradeServer.jar"]