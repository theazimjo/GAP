import { Repository } from 'typeorm';
import { Group } from '../../entities/group.entity';
import { Membership } from '../../entities/membership.entity';
import { UsersService } from '../users/users.service';
export declare class GroupsService {
    private groupsRepository;
    private membershipRepository;
    private usersService;
    constructor(groupsRepository: Repository<Group>, membershipRepository: Repository<Membership>, usersService: UsersService);
    createGroup(name: string, totalPool: number, contributionAmount: number, creatorId: number): Promise<Group>;
    getGroups(userId: number): Promise<Group[]>;
    getGroupDetail(groupId: number): Promise<Group>;
    addMember(groupId: number, phone: string, requesterId: number): Promise<Membership>;
}
//# sourceMappingURL=groups.service.d.ts.map