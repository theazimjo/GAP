"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.Membership = void 0;
const typeorm_1 = require("typeorm");
const user_entity_1 = require("./user.entity");
const group_entity_1 = require("./group.entity");
const payment_entity_1 = require("./payment.entity");
let Membership = class Membership {
    id;
    userId;
    groupId;
    role;
    user;
    group;
    payments;
};
exports.Membership = Membership;
__decorate([
    (0, typeorm_1.PrimaryGeneratedColumn)(),
    __metadata("design:type", Number)
], Membership.prototype, "id", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Membership.prototype, "userId", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Membership.prototype, "groupId", void 0);
__decorate([
    (0, typeorm_1.Column)({ default: 'member' }),
    __metadata("design:type", String)
], Membership.prototype, "role", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => user_entity_1.User, (user) => user.memberships),
    (0, typeorm_1.JoinColumn)({ name: 'userId' }),
    __metadata("design:type", user_entity_1.User)
], Membership.prototype, "user", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => group_entity_1.Group, (group) => group.members),
    (0, typeorm_1.JoinColumn)({ name: 'groupId' }),
    __metadata("design:type", group_entity_1.Group)
], Membership.prototype, "group", void 0);
__decorate([
    (0, typeorm_1.OneToMany)(() => payment_entity_1.Payment, (payment) => payment.membership),
    __metadata("design:type", Array)
], Membership.prototype, "payments", void 0);
exports.Membership = Membership = __decorate([
    (0, typeorm_1.Entity)('Membership'),
    (0, typeorm_1.Unique)(['userId', 'groupId'])
], Membership);
//# sourceMappingURL=membership.entity.js.map