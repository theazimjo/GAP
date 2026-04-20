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
exports.Turn = void 0;
const typeorm_1 = require("typeorm");
const group_entity_1 = require("./group.entity");
const user_entity_1 = require("./user.entity");
const payment_entity_1 = require("./payment.entity");
let Turn = class Turn {
    id;
    groupId;
    hostId;
    date;
    status;
    group;
    host;
    payments;
};
exports.Turn = Turn;
__decorate([
    (0, typeorm_1.PrimaryGeneratedColumn)(),
    __metadata("design:type", Number)
], Turn.prototype, "id", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Turn.prototype, "groupId", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Turn.prototype, "hostId", void 0);
__decorate([
    (0, typeorm_1.Column)({ type: 'timestamp' }),
    __metadata("design:type", Date)
], Turn.prototype, "date", void 0);
__decorate([
    (0, typeorm_1.Column)({ default: 'pending' }),
    __metadata("design:type", String)
], Turn.prototype, "status", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => group_entity_1.Group, (group) => group.turns),
    (0, typeorm_1.JoinColumn)({ name: 'groupId' }),
    __metadata("design:type", group_entity_1.Group)
], Turn.prototype, "group", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => user_entity_1.User, (user) => user.receivedGaps),
    (0, typeorm_1.JoinColumn)({ name: 'hostId' }),
    __metadata("design:type", user_entity_1.User)
], Turn.prototype, "host", void 0);
__decorate([
    (0, typeorm_1.OneToMany)(() => payment_entity_1.Payment, (payment) => payment.turn),
    __metadata("design:type", Array)
], Turn.prototype, "payments", void 0);
exports.Turn = Turn = __decorate([
    (0, typeorm_1.Entity)('Turn')
], Turn);
//# sourceMappingURL=turn.entity.js.map