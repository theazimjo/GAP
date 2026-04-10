import { Router } from 'express';
import { createGroup, getGroups, addMember, getGroupById } from '../controllers/group';
import { authenticate } from '../middleware/auth';

const router = Router();

// All group routes are protected
router.use(authenticate);

router.post('/', createGroup);
router.get('/', getGroups);
router.post('/add-member', addMember);
router.get('/:id', getGroupById);

export default router;
