import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, CreateDateColumn, JoinColumn } from 'typeorm';
import { Turn } from './turn.entity';
import { Membership } from './membership.entity';

@Entity('Payment')
export class Payment {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  turnId: number;

  @Column()
  membershipId: number;

  @Column({ type: 'decimal', precision: 12, scale: 2 })
  amount: number;

  @Column({ default: 'unpaid' })
  status: string;

  @CreateDateColumn()
  timestamp: Date;

  @ManyToOne(() => Turn, (turn) => turn.payments)
  @JoinColumn({ name: 'turnId' })
  turn: Turn;

  @ManyToOne(() => Membership, (membership) => membership.payments)
  @JoinColumn({ name: 'membershipId' })
  membership: Membership;
}
