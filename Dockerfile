FROM index.tenxcloud.com/shouhong/maven:3.3.9-jdk-8-v1.1
RUN mkdir /cloud-compile-src
COPY . /cloud-compile-src/
WORKDIR /cloud-compile-src
RUN	mvn package
RUN mv target/cloud-compile-0.1.0.jar /cloud-compile-0.1.0.jar
WORKDIR /
RUN rm -rf /cloud-compile-src
ENTRYPOINT ["java"]
CMD ["-jar", "/cloud-compile-0.1.0.jar"]