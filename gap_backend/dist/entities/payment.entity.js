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
exports.Payment = void 0;
const typeorm_1 = require("typeorm");
const turn_entity_1 = require("./turn.entity");
const membership_entity_1 = require("./membership.entity");
let Payment = class Payment {
    id;
    turnId;
    membershipId;
    amount;
    status;
    timestamp;
    turn;
    membership;
};
exports.Payment = Payment;
__decorate([
    (0, typeorm_1.PrimaryGeneratedColumn)(),
    __metadata("design:type", Number)
], Payment.prototype, "id", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Payment.prototype, "turnId", void 0);
__decorate([
    (0, typeorm_1.Column)(),
    __metadata("design:type", Number)
], Payment.prototype, "membershipId", void 0);
__decorate([
    (0, typeorm_1.Column)({ type: 'decimal', precision: 12, scale: 2 }),
    __metadata("design:type", Number)
], Payment.prototype, "amount", void 0);
__decorate([
    (0, typeorm_1.Column)({ default: 'unpaid' }),
    __metadata("design:type", String)
], Payment.prototype, "status", void 0);
__decorate([
    (0, typeorm_1.CreateDateColumn)(),
    __metadata("design:type", Date)
], Payment.prototype, "timestamp", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => turn_entity_1.Turn, (turn) => turn.payments),
    (0, typeorm_1.JoinColumn)({ name: 'turnId' }),
    __metadata("design:type", turn_entity_1.Turn)
], Payment.prototype, "turn", void 0);
__decorate([
    (0, typeorm_1.ManyToOne)(() => membership_entity_1.Membership, (membership) => membership.payments),
    (0, typeorm_1.JoinColumn)({ name: 'membershipId' }),
    __metadata("design:type", membership_entity_1.Membership)
], Payment.prototype, "membership", void 0);
exports.Payment = Payment = __decorate([
    (0, typeorm_1.Entity)('Payment')
], Payment);
//# sourceMappingURL=payment.entity.js.map