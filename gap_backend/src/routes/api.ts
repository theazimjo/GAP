import { Router } from 'express';
import { generateTurns, getTurns } from '../controllers/turn';
import { markPayment, getTurnPayments } from '../controllers/payment';
import { authenticate } from '../middleware/auth';

const router = Router();

router.use(authenticate);

// Turn routes
router.post('/generate', generateTurns);
router.get('/:groupId', getTurns);

// Payment routes
router.post('/pay', markPayment);
router.get('/payments/:turnId', getTurnPayments);

export default router;
