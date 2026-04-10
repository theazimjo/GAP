import { Router } from 'express';
import { register, login, requestOtp, verifyOtp, getMe } from '../controllers/auth';
import { authenticate } from '../middleware/auth';

const router = Router();

router.post('/register', register);
router.post('/login', login);
router.post('/request-otp', requestOtp);
router.post('/verify-otp', verifyOtp);
router.get('/me', authenticate, getMe);

export default router;
