import { Repository } from 'typeorm';
import { Turn } from '../../entities/turn.entity';
import { Membership } from '../../entities/membership.entity';
export declare class TurnsService {
    private turnsRepository;
    private membershipRepository;
    constructor(turnsRepository: Repository<Turn>, membershipRepository: Repository<Membership>);
    generateTurns(groupId: number, startDate: Date, frequencyDays: number, requesterId: number): Promise<Turn[]>;
    getTurns(groupId: number): Promise<Turn[]>;
}
//# sourceMappingURL=turns.service.d.ts.map