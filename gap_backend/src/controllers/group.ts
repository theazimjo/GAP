import { Response } from 'express';
import { prisma } from '../lib/prisma';
import { AuthRequest } from '../middleware/auth';

// const prisma = new PrismaClient(); // Handled by singleton

export const createGroup = async (req: AuthRequest, res: Response) => {
  try {
    const { name } = req.body;
    const userId = req.userId;

    if (!userId) return res.status(401).json({ message: 'Unauthorized' });

    if (!name) {
      return res.status(400).json({ message: 'Group name is required' });
    }

    const group = await prisma.group.create({
      data: {
        name,
        totalPool: 0,
        contributionAmount: 0,
        creatorId: userId,
        members: {
          create: {
            userId,
            role: 'admin',
          },
        },
      },
      include: {
        members: true,
      },
    });

    res.status(201).json({ message: 'Group created successfully', group });
  } catch (error) {
    console.error('Create group error:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
};

export const getGroups = async (req: AuthRequest, res: Response) => {
  try {
    const userId = req.userId;
    if (!userId) return res.status(401).json({ message: 'Unauthorized' });

    const groups = await prisma.group.findMany({
      where: {
        members: {
          some: {
            userId,
          },
        },
      },
      include: {
        creator: {
          select: { id: true, name: true, phone: true },
        },
        _count: {
          select: { members: true },
        },
      },
    });

    res.json(groups);
  } catch (error) {
    console.error('Get groups error:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
};

export const addMember = async (req: AuthRequest, res: Response) => {
  try {
    const { groupId, phone } = req.body;
    const userId = req.userId;

    if (!userId) return res.status(401).json({ message: 'Unauthorized' });

    // Check if requester is admin of the group
    const membership = await prisma.membership.findUnique({
      where: {
        userId_groupId: { userId, groupId },
      },
    });

    if (!membership || membership.role !== 'admin') {
      return res.status(403).json({ message: 'Only admins can add members' });
    }

    // Find user to add
    const userToAdd = await prisma.user.findUnique({ where: { phone } });
    if (!userToAdd) {
      return res.status(404).json({ message: 'User not found' });
    }

    // Check if already a member
    const existingMembership = await prisma.membership.findUnique({
      where: {
        userId_groupId: { userId: userToAdd.id, groupId },
      },
    });

    if (existingMembership) {
      return res.status(400).json({ message: 'User is already a member' });
    }

    const newMembership = await prisma.membership.create({
      data: {
        userId: userToAdd.id,
        groupId,
      },
    });

    res.status(201).json({ message: 'Member added successfully', membership: newMembership });
  } catch (error) {
    console.error('Add member error:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
};

export const getGroupById = async (req: AuthRequest, res: Response) => {
  try {
    const groupId = parseInt(req.params.id);
    const userId = req.userId;

    if (!userId) return res.status(401).json({ message: 'Unauthorized' });
    if (isNaN(groupId)) return res.status(400).json({ message: 'Invalid group ID' });

    // Ensure the requester is a member of this group
    const membership = await prisma.membership.findUnique({
      where: {
        userId_groupId: { userId, groupId },
      },
    });

    if (!membership) {
      return res.status(403).json({ message: 'You are not a member of this group' });
    }

    const group = await prisma.group.findUnique({
      where: { id: groupId },
      include: {
        creator: {
          select: { id: true, name: true, phone: true },
        },
        members: {
          include: {
            user: {
              select: { id: true, name: true, phone: true, avatarUrl: true },
            },
          },
        },
      },
    });

    if (!group) return res.status(404).json({ message: 'Group not found' });

    res.json(group);
  } catch (error) {
    console.error('Get group detail error:', error);
    res.status(500).json({ message: 'Internal server error' });
  }
};
