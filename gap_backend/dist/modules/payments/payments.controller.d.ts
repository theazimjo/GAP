import { PaymentsService } from './payments.service';
export declare class PaymentsController {
    private readonly paymentsService;
    constructor(paymentsService: PaymentsService);
    pay(body: any, req: any): Promise<import("../../entities/payment.entity").Payment>;
    getPayments(turnId: string): Promise<import("../../entities/payment.entity").Payment[]>;
}
//# sourceMappingURL=payments.controller.d.ts.map