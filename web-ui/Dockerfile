FROM nginx:stable-alpine

COPY build/ /usr/share/nginx/html

# expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]