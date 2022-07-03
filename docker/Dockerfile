FROM ubuntu:20.04
MAINTAINER muyi zcy_nemo@aliyun.com
LABEL Description="Ubuntu base image 1.0 <路漫漫其修远兮，吾将上下而求索>"

ENV LANG C.UTF-8

ENV TZ=Asia/Shanghai

ARG JAVA_VERSION=8
ARG JAVA_RELEASE=JDK

WORKDIR /home/app

VOLUME /data

# 设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN bash -c ' \
    set -euxo pipefail && \
    apt-get update && \
    pkg="openjdk-$JAVA_VERSION"; \
    if [ "$JAVA_RELEASE" = "JDK" ]; then \
        pkg="$pkg-jdk-headless"; \
    else \
        pkg="$pkg-jre-headless"; \
    fi; \
    apt-get install -y --no-install-recommends "$pkg" && \
    apt-get clean'

ENV JAVA_HOME=/usr

ENV DEBIAN_FRONTEND=noninteractive

ADD tech-muyi-web-1.0-SNAPSHOT.jar /home/app/app.jar
ADD start.sh /home/app/start.sh

RUN bash -c 'chmod +x /home/app/start.sh'

CMD /home/app/start.sh