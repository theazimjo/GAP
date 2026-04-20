import { TurnsService } from './turns.service';
export declare class TurnsController {
    private readonly turnsService;
    constructor(turnsService: TurnsService);
    generate(body: any, req: any): Promise<import("../../entities/turn.entity").Turn[]>;
    findByGroup(groupId: string): Promise<import("../../entities/turn.entity").Turn[]>;
}
//# sourceMappingURL=turns.controller.d.ts.map