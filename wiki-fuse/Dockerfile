FROM ubuntu:latest

RUN useradd -u 1000 user
RUN apt-get update && apt-get -y install git python3 python3-dev python3-pip libattr1-dev libfuse-dev pkg-config bash fuse
RUN git clone https://github.com/JelteF/dokuwikifuse /home/user/doku && chown user:user -R /home/user/doku
WORKDIR /home/user/doku
COPY --chown=user:user config.py .
RUN pip3 install -r requirements.txt
USER 1000

ENTRYPOINT ["python3", "dokuwikifuse.py"]
