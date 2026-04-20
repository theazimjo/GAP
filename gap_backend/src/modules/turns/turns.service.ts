import { Injectable, ForbiddenException, BadRequestException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Turn } from '../../entities/turn.entity';
import { Membership } from '../../entities/membership.entity';

@Injectable()
export class TurnsService {
  constructor(
    @InjectRepository(Turn)
    private turnsRepository: Repository<Turn>,
    @InjectRepository(Membership)
    private membershipRepository: Repository<Membership>,
  ) {}

  async generateTurns(groupId: number, startDate: Date, frequencyDays: number, requesterId: number) {
    // Verify admin
    const membership = await this.membershipRepository.findOne({
      where: { userId: requesterId, groupId },
    });

    if (!membership || membership.role !== 'admin') {
      throw new ForbiddenException('Only admins can generate turns');
    }

    const members = await this.membershipRepository.find({
      where: { groupId },
      relations: ['user'],
    });

    if (members.length === 0) {
      throw new BadRequestException('No members in group');
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

  async getTurns(groupId: number) {
    return this.turnsRepository.find({
      where: { groupId },
      relations: ['host'],
      order: { date: 'ASC' },
    });
  }
}
