import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, OneToMany, JoinColumn } from 'typeorm';
import { Group } from './group.entity';
import { User } from './user.entity';
import { Payment } from './payment.entity';

@Entity('Turn')
export class Turn {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  groupId: number;

  @Column()
  hostId: number;

  @Column({ type: 'timestamp' })
  date: Date;

  @Column({ default: 'pending' })
  status: string;

  @ManyToOne(() => Group, (group) => group.turns)
  @JoinColumn({ name: 'groupId' })
  group: Group;

  @ManyToOne(() => User, (user) => user.receivedGaps)
  @JoinColumn({ name: 'hostId' })
  host: User;

  @OneToMany(() => Payment, (payment) => payment.turn)
  payments: Payment[];
}
