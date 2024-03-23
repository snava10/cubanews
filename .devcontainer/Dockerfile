# at some point, we can change this to a clean ubuntu or linux image
FROM node:bookworm

# Install Java and graphviz 
RUN apt update
RUN apt --assume-yes install default-jre

# Install Python dependencies
RUN apt install python3-pip -y
COPY python-scrapper/requirements.txt /tmp/
RUN pip3 install --user -r /tmp/requirements.txt --break-system-packages
