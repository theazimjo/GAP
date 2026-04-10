"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const group_1 = require("../controllers/group");
const auth_1 = require("../middleware/auth");
const router = (0, express_1.Router)();
// All group routes are protected
router.use(auth_1.authenticate);
router.post('/', group_1.createGroup);
router.get('/', group_1.getGroups);
router.post('/add-member', group_1.addMember);
exports.default = router;
//# sourceMappingURL=group.js.map