import { User } from './user.entity';
import { Group } from './group.entity';
import { Payment } from './payment.entity';
export declare class Membership {
    id: number;
    userId: number;
    groupId: number;
    role: string;
    user: User;
    group: Group;
    payments: Payment[];
}
//# sourceMappingURL=membership.entity.d.ts.map