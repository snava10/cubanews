FROM nginx:stable-alpine
ARG BUILD
RUN echo ${BUILD}
COPY ${BUILD} /usr/share/nginx/html

# expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]