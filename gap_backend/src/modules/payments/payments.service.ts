import { Injectable, ForbiddenException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Payment } from '../../entities/payment.entity';
import { Membership } from '../../entities/membership.entity';
import { Turn } from '../../entities/turn.entity';

@Injectable()
export class PaymentsService {
  constructor(
    @InjectRepository(Payment)
    private paymentRepository: Repository<Payment>,
    @InjectRepository(Membership)
    private membershipRepository: Repository<Membership>,
    @InjectRepository(Turn)
    private turnRepository: Repository<Turn>,
  ) {}

  async markPayment(turnId: number, amount: number, status: string, userId: number) {
    const turn = await this.turnRepository.findOne({
      where: { id: turnId },
      relations: ['group'],
    });

    if (!turn) throw new NotFoundException('Turn not found');

    const membership = await this.membershipRepository.findOne({
      where: { userId, groupId: turn.groupId },
    });

    if (!membership) throw new ForbiddenException('You are not a member of this group');

    // If verifying, check if admin
    if (status === 'verified' && membership.role !== 'admin') {
      throw new ForbiddenException('Only admins can verify payments');
    }

    let payment = await this.paymentRepository.findOne({
      where: { turnId, membershipId: membership.id },
    });

    if (payment) {
      payment.amount = amount;
      payment.status = status;
      payment.timestamp = new Date();
    } else {
      payment = this.paymentRepository.create({
        turnId,
        membershipId: membership.id,
        amount,
        status,
      });
    }

    return this.paymentRepository.save(payment);
  }

  async getTurnPayments(turnId: number) {
    return this.paymentRepository.find({
      where: { turnId },
      relations: ['membership', 'membership.user'],
    });
  }
}
