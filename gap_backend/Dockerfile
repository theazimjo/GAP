FROM node:20-alpine

WORKDIR /app

COPY package*.json ./
COPY prisma ./prisma/

RUN npm install

COPY . .

RUN DATABASE_URL="postgresql://postgres:postgres@localhost:5432/db" npx prisma generate
RUN npm run build || true

EXPOSE 4000

CMD ["npm", "run", "dev"]
