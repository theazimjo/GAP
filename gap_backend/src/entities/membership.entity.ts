import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, OneToMany, Unique, JoinColumn } from 'typeorm';
import { User } from './user.entity';
import { Group } from './group.entity';
import { Payment } from './payment.entity';

@Entity('Membership')
@Unique(['userId', 'groupId'])
export class Membership {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  userId: number;

  @Column()
  groupId: number;

  @Column({ default: 'member' })
  role: string;

  @ManyToOne(() => User, (user) => user.memberships)
  @JoinColumn({ name: 'userId' })
  user: User;

  @ManyToOne(() => Group, (group) => group.members)
  @JoinColumn({ name: 'groupId' })
  group: Group;

  @OneToMany(() => Payment, (payment) => payment.membership)
  payments: Payment[];
}
