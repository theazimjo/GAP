# ============================
# Stage 1: Builder
# ============================
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Copy Prisma schema
COPY prisma ./prisma/

# Install ALL dependencies (including devDependencies needed for typescript)
RUN npm install

# Copy application code
COPY . .

# Generate Prisma client
RUN DATABASE_URL="postgresql://postgres:postgres@localhost:5432/db" npx prisma generate

# Build TypeScript to the dist/ folder
RUN npm run build

# ============================
# Stage 2: Production Runner
# ============================
FROM node:20-alpine AS runner

WORKDIR /app

# Only copy necessary files from Builder
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/prisma ./prisma
COPY --from=builder /app/node_modules ./node_modules

EXPOSE 4000

# The startup command is defined in docker-compose.yml to run migrations first
CMD ["npm", "start"]
