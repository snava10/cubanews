# cubanews

## Web App packaging

An easy way of packaging and deploying an app is using Docker.

### Build the image

Run ```npm run build; docker build --tag cubanews-web:local .```

Examining the the Dockerfile notice how it is running NGINX with the built react files inside. The key commands are:

```
COPY ./nginx.conf /etc/nginx/nginx.conf
COPY build/ /usr/share/nginx/html
```

The container exposes port 80 with the command ```EXPOSE 80```

## API packaging

## Nginx Proxy

In order to avoid CORS we need to use NGING as a reverse proxy. The external requests will always go to the Web App url on port 80, for example localhost:80
However the API is running in a different container and in a different port (8080). Every time the Web client tries to call the API the browser will block
the request because is going to a different base Url (A different port counts as a different base url)

Nginx config server section.
```
server {
        listen       80;
        listen  [::]:80;        
        include /etc/nginx/mime.types;

        location / {
            root /usr/share/nginx/html/;
            index index.html;
            try_files $uri $uri/ /index.html;
        }

        location /api {
            proxy_pass http://api:8080;
        }
    }
```

The default location ``/`` will server the default requests to the website and it will return the static files (the react code)
The second location ``/api`` will match every URL that after the domain has /api, whihc is all the calls from the web client to our API.
By using the proxy_pass instruction from Nginx we can redirect those requests to our rest api.
``http://api:8080`` is the url of the server, which is defined by the name of the container in the docker compose file.

What happens is that the web and api, running on docker compose are like two machines in a local network. In that local network there is a DNS 
that works with the name of the container specified in the docker compose. So, Nginx can forward the request to the Api container using its name.

Another important detail is the ``REACT_APP_BASE_URL`` in the prod.env, in order to run it on a server and make external requests function it needs
to be the Url of the machine (or IP, but AWS will give you a dns for the changing IP address). This should be changed to the domain for a propper deployment.

