FROM nginx:alpine

COPY index.html /usr/share/nginx/html/index.html
COPY logo.png /usr/share/nginx/html/logo.png
COPY ic-16.ico /usr/share/nginx/html/ic-16.ico
COPY ic-32.ico /usr/share/nginx/html/ic-32.ico
COPY ic-64.ico /usr/share/nginx/html/ic-64.ico
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost/health || exit 1
