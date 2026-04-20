import { User } from './user.entity';
import { Membership } from './membership.entity';
import { Turn } from './turn.entity';
export declare class Group {
    id: number;
    name: string;
    totalPool: number;
    contributionAmount: number;
    createdAt: Date;
    creatorId: number;
    creator: User;
    members: Membership[];
    turns: Turn[];
}
//# sourceMappingURL=group.entity.d.ts.map