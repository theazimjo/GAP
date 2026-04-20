import { Turn } from './turn.entity';
import { Membership } from './membership.entity';
export declare class Payment {
    id: number;
    turnId: number;
    membershipId: number;
    amount: number;
    status: string;
    timestamp: Date;
    turn: Turn;
    membership: Membership;
}
//# sourceMappingURL=payment.entity.d.ts.map