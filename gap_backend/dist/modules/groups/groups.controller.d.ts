import { GroupsService } from './groups.service';
export declare class GroupsController {
    private readonly groupsService;
    constructor(groupsService: GroupsService);
    create(body: any, req: any): Promise<import("../../entities/group.entity").Group>;
    findAll(req: any): Promise<import("../../entities/group.entity").Group[]>;
    findOne(id: string): Promise<import("../../entities/group.entity").Group>;
    addMember(body: any, req: any): Promise<import("../../entities/membership.entity").Membership>;
}
//# sourceMappingURL=groups.controller.d.ts.map