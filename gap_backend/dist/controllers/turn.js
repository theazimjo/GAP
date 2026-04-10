"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getTurns = exports.generateTurns = void 0;
const prisma_1 = require("../lib/prisma");
// const prisma = new PrismaClient(); // Handled by singleton
const generateTurns = async (req, res) => {
    try {
        const { groupId, startDate, frequencyDays } = req.body;
        const userId = req.userId;
        if (!userId)
            return res.status(401).json({ message: 'Unauthorized' });
        // Verify admin
        const membership = await prisma_1.prisma.membership.findUnique({
            where: { userId_groupId: { userId, groupId } },
        });
        if (!membership || membership.role !== 'admin') {
            return res.status(403).json({ message: 'Only admins can generate turns' });
        }
        const members = await prisma_1.prisma.membership.findMany({
            where: { groupId },
            include: { user: true },
        });
        if (members.length === 0) {
            return res.status(400).json({ message: 'No members in group' });
        }
        const start = new Date(startDate);
        const turnsData = members.map((member, index) => {
            const turnDate = new Date(start);
            turnDate.setDate(start.getDate() + (index * frequencyDays));
            return {
                groupId,
                hostId: member.userId,
                date: turnDate,
                status: 'pending',
            };
        });
        // Clean existing turns first (optional, but safer for re-generation)
        await prisma_1.prisma.turn.deleteMany({ where: { groupId } });
        const turns = await prisma_1.prisma.turn.createMany({
            data: turnsData,
        });
        res.status(201).json({ message: 'Turns generated successfully', count: turnsData.length });
    }
    catch (error) {
        console.error('Generate turns error:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
};
exports.generateTurns = generateTurns;
const getTurns = async (req, res) => {
    try {
        const { groupId } = req.params;
        const userId = req.userId;
        if (!groupId)
            return res.status(400).json({ message: 'Group ID is required' });
        if (!userId)
            return res.status(401).json({ message: 'Unauthorized' });
        const turns = await prisma_1.prisma.turn.findMany({
            where: { groupId: parseInt(groupId) },
            include: {
                host: { select: { id: true, name: true, phone: true } },
            },
            orderBy: { date: 'asc' },
        });
        res.json(turns);
    }
    catch (error) {
        console.error('Get turns error:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
};
exports.getTurns = getTurns;
//# sourceMappingURL=turn.js.map