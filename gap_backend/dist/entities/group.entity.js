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
exports.Group = void 0;
const typeorm_1 = require("typeorm");
const user_entity_1 = require("./user.entity");
const membership_entity_1 = require("./membership.entity");
const turn_entity_1 = require("./turn.entity");
let Group = class Group {
    id;
    name;
    totalPool;
    contributionAmount;
    createdAt;
    creatorId;
    creator;
    members;
    turns;
};
exports.Group = Group;
__decorate([
    (0, typeorm_1.PrimaryGeneratedColumn)(),
    __metadata("design:type", Number)
], Group.prototype, "id", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", String)
], Group.prototype, "name", void 0);
__decorate([
    (0, typeorm_1.Column)({ type: 'decimal', precision: 12, scale: 2 }),
    __metadata("design:type", Number)
], Group.prototype, "totalPool", void 0);
__decorate([
    (0, typeorm_1.Column)({ type: 'decimal', precision: 12, scale: 2 }),
    __metadata("design:type", Number)
], Group.prototype, "contributionAmount", void 0);
__decorate([
    (0, typeorm_1.CreateDateColumn)(),
    __metadata("design:type", Date)
], Group.prototype, "createdAt", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Group.prototype, "creatorId", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => user_entity_1.User, (user) => user.ownedGroups),
    (0, typeorm_1.JoinColumn)({ name: 'creatorId' }),
    __metadata("design:type", user_entity_1.User)
], Group.prototype, "creator", void 0);
__decorate([
    (0, typeorm_1.OneToMany)(() => membership_entity_1.Membership, (membership) => membership.group),
    __metadata("design:type", Array)
], Group.prototype, "members", void 0);
__decorate([
    (0, typeorm_1.OneToMany)(() => turn_entity_1.Turn, (turn) => turn.group),
    __metadata("design:type", Array)
], Group.prototype, "turns", void 0);
exports.Group = Group = __decorate([
    (0, typeorm_1.Entity)('Group')
], Group);
//# sourceMappingURL=group.entity.js.map