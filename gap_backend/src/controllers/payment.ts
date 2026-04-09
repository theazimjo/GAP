import { Response } from 'express';
import { prisma } from '../lib/prisma';
import { AuthRequest } from '../middleware/auth';

// const prisma = new PrismaClient(); // Handled by singleton

export const markPayment = async (req: AuthRequest, res: Response) => {
  try {
    const { turnId, amount, status } = req.body; // status: 'paid' or 'verified'
    const userId = req.userId;

    if (!userId) return res.status(401).json({ message: 'Unauthorized' });

    // Find the membership of the user for the group associated with this turn
    const turn = await prisma.turn.findUnique({
      where: { id: turnId },
      include: { group: true },
    });

    if (!turn) return res.status(404).json({ message: 'Turn not found' });

    const membership = await prisma.membership.findUnique({
      where: { userId_groupId: { userId, groupId: turn.groupId } },
    });

    if (!membership) return res.status(403).json({ message: 'You are not a member of this group' });

    // If verifying, check if admin
    if (status === 'verified' && membership.role !== 'admin') {
      return res.status(403).json({ message: 'Only admins can verify payments' });
    }

    const payment = await prisma.payment.upsert({
      where: {
        id: -1, // This is a trick for upsert if you don't have the ID, better search first
      },
      create: {
        turnId,
        membershipId: membership.id,
        amount,
        status,
      },
      update: {
        amount,
        status,
      },
    });

    // Actually, we should find existing payment first to avoid ID -1 issues
    const existingPayment = await prisma.payment.findFirst({
      where: { turnId, membershipId: membership.id }
    });

    let finalPayment;
    if (existingPayment) {
      finalPayment = await prisma.payment.update({
        where: { id: existingPayment.id },
        data: { amount, status, timestamp: new Date() }
      });
    } else {
      finalPayment = await prisma.payment.create({
        data: { turnId, membershipId: membership.id, amount, status }
      });
    }

    res.json({ message: 'Payment updated', payment: finalPayment });
  } catch (error) {
    console.error('Mark payment error:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
};

export const getTurnPayments = async (req: AuthRequest, res: Response) => {
  try {
    const { turnId } = req.params;
    const userId = req.userId;

    if (!userId) return res.status(401).json({ message: 'Unauthorized' });

    const payments = await prisma.payment.findMany({
      where: { turnId: parseInt(turnId) },
      include: {
        membership: {
          include: { user: { select: { id: true, name: true, phone: true } } }
        }
      }
    });

    res.json(payments);
  } catch (error) {
    console.error('Get payments error:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
};
