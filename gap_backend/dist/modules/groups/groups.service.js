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
exports.GroupsService = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const typeorm_2 = require("typeorm");
const group_entity_1 = require("../../entities/group.entity");
const membership_entity_1 = require("../../entities/membership.entity");
const users_service_1 = require("../users/users.service");
let GroupsService = class GroupsService {
    groupsRepository;
    membershipRepository;
    usersService;
    constructor(groupsRepository, membershipRepository, usersService) {
        this.groupsRepository = groupsRepository;
        this.membershipRepository = membershipRepository;
        this.usersService = usersService;
    }
    async createGroup(name, totalPool, contributionAmount, creatorId) {
        const group = this.groupsRepository.create({
            name,
            totalPool,
            contributionAmount,
            creatorId,
        });
        const savedGroup = await this.groupsRepository.save(group);
        // Auto add creator as admin
        await this.membershipRepository.save({
            userId: creatorId,
            groupId: savedGroup.id,
            role: 'admin',
        });
        return savedGroup;
    }
    async getGroups(userId) {
        return this.groupsRepository
            .createQueryBuilder('group')
            .innerJoin('group.members', 'membership')
            .where('membership.userId = :userId', { userId })
            .getMany();
    }
    async getGroupDetail(groupId) {
        const group = await this.groupsRepository.findOne({
            where: { id: groupId },
            relations: ['members', 'members.user'],
        });
        if (!group)
            throw new common_1.NotFoundException('Group not found');
        return group;
    }
    async addMember(groupId, phone, requesterId) {
        // Verify requester is admin
        const requesterMembership = await this.membershipRepository.findOne({
            where: { userId: requesterId, groupId },
        });
        if (!requesterMembership || requesterMembership.role !== 'admin') {
            throw new common_1.ForbiddenException('Only admins can add members');
        }
        const userToAdd = await this.usersService.getOrCreatePlaceholder(phone);
        // Check if already a member
        const existing = await this.membershipRepository.findOne({
            where: { userId: userToAdd.id, groupId },
        });
        if (existing)
            return existing;
        return this.membershipRepository.save({
            userId: userToAdd.id,
            groupId,
            role: 'member',
        });
    }
};
exports.GroupsService = GroupsService;
exports.GroupsService = GroupsService = __decorate([
    (0, common_1.Injectable)(),
    __param(0, (0, typeorm_1.InjectRepository)(group_entity_1.Group)),
    __param(1, (0, typeorm_1.InjectRepository)(membership_entity_1.Membership)),
    __metadata("design:paramtypes", [typeorm_2.Repository,
        typeorm_2.Repository,
        users_service_1.UsersService])
], GroupsService);
//# sourceMappingURL=groups.service.js.map