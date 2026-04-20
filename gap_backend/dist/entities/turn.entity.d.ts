import { Group } from './group.entity';
import { User } from './user.entity';
import { Payment } from './payment.entity';
export declare class Turn {
    id: number;
    groupId: number;
    hostId: number;
    date: Date;
    status: string;
    group: Group;
    host: User;
    payments: Payment[];
}
//# sourceMappingURL=turn.entity.d.ts.map