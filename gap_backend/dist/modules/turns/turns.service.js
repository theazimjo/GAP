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
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.TurnsService = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const typeorm_2 = require("typeorm");
const turn_entity_1 = require("../../entities/turn.entity");
const membership_entity_1 = require("../../entities/membership.entity");
let TurnsService = class TurnsService {
    turnsRepository;
    membershipRepository;
    constructor(turnsRepository, membershipRepository) {
        this.turnsRepository = turnsRepository;
        this.membershipRepository = membershipRepository;
    }
    async generateTurns(groupId, startDate, frequencyDays, requesterId) {
        // Verify admin
        const membership = await this.membershipRepository.findOne({
            where: { userId: requesterId, groupId },
        });
        if (!membership || membership.role !== 'admin') {
            throw new common_1.ForbiddenException('Only admins can generate turns');
        }
        const members = await this.membershipRepository.find({
            where: { groupId },
            relations: ['user'],
        });
        if (members.length === 0) {
            throw new common_1.BadRequestException('No members in group');
        }
        // Clean existing turns
        await this.turnsRepository.delete({ groupId });
        const start = new Date(startDate);
        const turns = members.map((member, index) => {
            const turnDate = new Date(start);
            turnDate.setDate(start.getDate() + (index * frequencyDays));
            return this.turnsRepository.create({
                groupId,
                hostId: member.userId,
                date: turnDate,
                status: 'pending',
            });
        });
        return this.turnsRepository.save(turns);
    }
    async getTurns(groupId) {
        return this.turnsRepository.find({
            where: { groupId },
            relations: ['host'],
            order: { date: 'ASC' },
        });
    }
};
exports.TurnsService = TurnsService;
exports.TurnsService = TurnsService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, typeorm_1.InjectRepository)(turn_entity_1.Turn)),
    __param(1, (0, typeorm_1.InjectRepository)(membership_entity_1.Membership)),
    __metadata("design:paramtypes", [typeorm_2.Repository,
        typeorm_2.Repository])
], TurnsService);
//# sourceMappingURL=turns.service.js.map