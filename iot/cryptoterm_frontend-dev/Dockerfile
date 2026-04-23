# Stage 1: build React app
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2: serve with nginx
FROM nginx:stable-alpine

# Копируем билды React в nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Настроим прокси для API
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

