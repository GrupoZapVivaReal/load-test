FROM vivareal/base-images:alpine-3.5-java-8-jdk

ARG ARTIFACT

WORKDIR /app

COPY . /app

RUN ./gradlew compileGatlingScala

ENTRYPOINT ["./gradlew", "clean", "gatlingRun", "uploadReport"]
