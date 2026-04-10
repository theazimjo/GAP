"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.verifyOtp = exports.requestOtp = exports.login = exports.register = void 0;
const prisma_1 = require("../lib/prisma");
const bcrypt_1 = __importDefault(require("bcrypt"));
const jwt_1 = require("../utils/jwt");
// const prisma = new PrismaClient(); // Handled by singleton
const register = async (req, res) => {
    try {
        const { name, phone, password } = req.body;
        if (!name || !phone || !password) {
            return res.status(400).json({ message: 'Name, phone and password are required' });
        }
        const existingUser = await prisma_1.prisma.user.findUnique({ where: { phone } });
        if (existingUser) {
            return res.status(400).json({ message: 'User with this phone already exists' });
        }
        const passwordHash = await bcrypt_1.default.hash(password, 10);
        const user = await prisma_1.prisma.user.create({
            data: {
                name,
                phone,
                passwordHash,
            },
        });
        const token = (0, jwt_1.signToken)({ userId: user.id });
        res.status(201).json({
            message: 'User registered successfully',
            token,
            user: { id: user.id, name: user.name, phone: user.phone },
        });
    }
    catch (error) {
        console.error('Registration error:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
};
exports.register = register;
const login = async (req, res) => {
    try {
        const { phone, password } = req.body;
        if (!phone || !password) {
            return res.status(400).json({ message: 'Phone and password are required' });
        }
        const user = await prisma_1.prisma.user.findUnique({ where: { phone } });
        if (!user) {
            return res.status(400).json({ message: 'Invalid phone or password' });
        }
        const isPasswordValid = await bcrypt_1.default.compare(password, user.passwordHash);
        if (!isPasswordValid) {
            return res.status(400).json({ message: 'Invalid phone or password' });
        }
        const token = (0, jwt_1.signToken)({ userId: user.id });
        res.json({
            message: 'Login successful',
            token,
            user: { id: user.id, name: user.name, phone: user.phone },
        });
    }
    catch (error) {
        console.error('Login error:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
};
exports.login = login;
const requestOtp = async (req, res) => {
    try {
        const { phone } = req.body;
        if (!phone) {
            return res.status(400).json({ message: 'Phone number is required' });
        }
        // Generate 6 digit OTP
        const otpCode = Math.floor(100000 + Math.random() * 900000).toString();
        const otpExpiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutes
        const user = await prisma_1.prisma.user.upsert({
            where: { phone },
            update: { otpCode, otpExpiresAt },
            create: {
                phone,
                name: 'New User', // Placeholder name for OTP-only registration
                passwordHash: '', // Password not used for OTP flow
                otpCode,
                otpExpiresAt
            },
        });
        // In a real scenario, we would NOT return the OTP here.
        // For development (the "send to self" hack), we return it.
        res.json({
            message: 'OTP sent successfully',
            otpCode, // SECURE: In production, remove this!
        });
    }
    catch (error) {
        console.error('OTP request error:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
};
exports.requestOtp = requestOtp;
const verifyOtp = async (req, res) => {
    try {
        const { phone, otpCode } = req.body;
        if (!phone || !otpCode) {
            return res.status(400).json({ message: 'Phone and OTP code are required' });
        }
        const user = await prisma_1.prisma.user.findUnique({ where: { phone } });
        if (!user || user.otpCode !== otpCode || !user.otpExpiresAt || user.otpExpiresAt < new Date()) {
            return res.status(400).json({ message: 'Invalid or expired OTP' });
        }
        // Clear OTP after successful verification
        await prisma_1.prisma.user.update({
            where: { id: user.id },
            data: { otpCode: null, otpExpiresAt: null },
        });
        const token = (0, jwt_1.signToken)({ userId: user.id });
        res.json({
            message: 'OTP verified successfully',
            token,
            user: { id: user.id, name: user.name, phone: user.phone },
        });
    }
    catch (error) {
        console.error('OTP verification error:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
};
exports.verifyOtp = verifyOtp;
//# sourceMappingURL=auth.js.map