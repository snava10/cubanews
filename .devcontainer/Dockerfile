# at some point, we can change this to a clean ubuntu or linux image
FROM node:bookworm

# Install Java and graphviz 
RUN apt update
RUN apt --assume-yes install default-jre

# Scrapy dependencies
# RUN apt-get install python3 python3-dev python3-pip libxml2-dev libxslt1-dev zlib1g-dev libffi-dev libssl-dev

# Install Python dependencies
RUN apt install python3-pip -y
RUN apt install python3.11-venv -y
COPY python-scrapper/requirements.txt /tmp/
RUN pip3 install --user -r /tmp/requirements.txt --break-system-packages

# Having to use a python virtual environment to get scrapy working
# 1. create a virtual env. Stores env dirrectory in python-scrapper backend
# 2. activate the virtual env. `source scrapy-env/bin/activate`
# 3. pip install requirements