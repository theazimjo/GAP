import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, ManyToOne, OneToMany, JoinColumn } from 'typeorm';
import { User } from './user.entity';
import { Membership } from './membership.entity';
import { Turn } from './turn.entity';

@Entity('Group')
export class Group {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  name: string;

  @Column({ type: 'decimal', precision: 12, scale: 2 })
  totalPool: number;

  @Column({ type: 'decimal', precision: 12, scale: 2 })
  contributionAmount: number;

  @CreateDateColumn()
  createdAt: Date;

  @Column()
  creatorId: number;

  @ManyToOne(() => User, (user) => user.ownedGroups)
  @JoinColumn({ name: 'creatorId' })
  creator: User;

  @OneToMany(() => Membership, (membership) => membership.group)
  members: Membership[];

  @OneToMany(() => Turn, (turn) => turn.group)
  turns: Turn[];
}
