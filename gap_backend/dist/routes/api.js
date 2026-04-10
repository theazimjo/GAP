"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const turn_1 = require("../controllers/turn");
const payment_1 = require("../controllers/payment");
const auth_1 = require("../middleware/auth");
const router = (0, express_1.Router)();
router.use(auth_1.authenticate);
// Turn routes
router.post('/generate', turn_1.generateTurns);
router.get('/:groupId', turn_1.getTurns);
// Payment routes
router.post('/pay', payment_1.markPayment);
router.get('/payments/:turnId', payment_1.getTurnPayments);
exports.default = router;
//# sourceMappingURL=api.js.map