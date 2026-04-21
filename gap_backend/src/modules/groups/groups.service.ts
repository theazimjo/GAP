import { Injectable, ForbiddenException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Group } from '../../entities/group.entity';
import { Membership } from '../../entities/membership.entity';
import { UsersService } from '../users/users.service';

@Injectable()
export class GroupsService {
  constructor(
    @InjectRepository(Group)
    private groupsRepository: Repository<Group>,
    @InjectRepository(Membership)
    private membershipRepository: Repository<Membership>,
    private usersService: UsersService,
  ) {}

  async createGroup(
    name: string, 
    totalPool: number, 
    contributionAmount: number, 
    creatorId: number,
    metadata?: {
      emoji?: string;
      description?: string;
      isAmountOptional?: boolean;
      meetingDays?: string;
      selectionMethod?: string;
    }
  ) {
    const group = this.groupsRepository.create({
      name,
      totalPool,
      contributionAmount,
      creatorId,
      ...metadata
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

  async getGroups(userId: number) {
    return this.groupsRepository
      .createQueryBuilder('group')
      .innerJoin('group.members', 'membership')
      .where('membership.userId = :userId', { userId })
      .getMany();
  }

  async getGroupDetail(groupId: number) {
    const group = await this.groupsRepository.findOne({
      where: { id: groupId },
      relations: ['members', 'members.user'],
    });

    if (!group) throw new NotFoundException('Group not found');
    return group;
  }

  async addMember(groupId: number, phone: string, requesterId: number) {
    // Verify requester is admin
    const requesterMembership = await this.membershipRepository.findOne({
      where: { userId: requesterId, groupId },
    });

    if (!requesterMembership || requesterMembership.role !== 'admin') {
      throw new ForbiddenException('Only admins can add members');
    }

    const userToAdd = await this.usersService.getOrCreatePlaceholder(phone);

    // Check if already a member
    const existing = await this.membershipRepository.findOne({
      where: { userId: userToAdd.id, groupId },
    });

    if (existing) return existing;

    return this.membershipRepository.save({
      userId: userToAdd.id,
      groupId,
      role: 'member',
    });
  }
}
