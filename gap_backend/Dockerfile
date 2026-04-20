# Build stage
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Build the NestJS application
RUN npm run build

# Production stage
FROM node:18-alpine

WORKDIR /app

# Copy production dependencies from package*.json
COPY package*.json ./
RUN npm install --only=production

# Copy built assets from builder
COPY --from=builder /app/dist ./dist

# Set default Environment variables
ENV PORT=2030
ENV NODE_ENV=production

EXPOSE 2030

# Start the application
CMD ["node", "dist/main.js"]
