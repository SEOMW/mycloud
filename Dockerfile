FROM nginx:1.25.1

ENV REPO=static-web-template
ENV TAG_VERSION=1.1

RUN curl -LJO https://github.com/SEOMW/https---github.com-SEOMW-docker_example/archive/refs/tags/v1.1.tar.gz
RUN tar -zxvf ${REPO}-${TAG_VERSION}.tar.gz
RUN mv ${REPO}-${TAG_VERSION} /usr/share/nginx/html