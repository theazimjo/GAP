import express, { Request, Response } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import { prisma } from './lib/prisma';
import authRoutes from './routes/auth';
import groupRoutes from './routes/group';
import apiRoutes from './routes/api';

dotenv.config();

const app = express();
// const prisma = new PrismaClient(); // Handled by singleton
const port = process.env.PORT || 4000;

app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/groups', groupRoutes);
app.use('/api', apiRoutes);

app.get('/', (req: Request, res: Response) => {
  res.json({ message: 'Welcome to Gap API', version: '1.0.0' });
});

// Basic health check
app.get('/health', async (req: Request, res: Response) => {
  try {
    await prisma.$queryRaw`SELECT 1`;
    res.json({ status: 'ok', database: 'connected' });
  } catch (error) {
    res.status(500).json({ status: 'error', database: 'disconnected' });
  }
});

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
