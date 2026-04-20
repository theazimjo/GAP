import { Group } from './group.entity';
import { Membership } from './membership.entity';
import { Turn } from './turn.entity';
export declare class User {
    id: number;
    name: string;
    phone: string;
    email: string;
    telegramId: string;
    passwordHash: string;
    avatarUrl: string;
    otpCode: string;
    otpExpiresAt: Date;
    createdAt: Date;
    updatedAt: Date;
    ownedGroups: Group[];
    memberships: Membership[];
    receivedGaps: Turn[];
}
//# sourceMappingURL=user.entity.d.ts.map