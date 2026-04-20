import { Repository } from 'typeorm';
import { Payment } from '../../entities/payment.entity';
import { Membership } from '../../entities/membership.entity';
import { Turn } from '../../entities/turn.entity';
export declare class PaymentsService {
    private paymentRepository;
    private membershipRepository;
    private turnRepository;
    constructor(paymentRepository: Repository<Payment>, membershipRepository: Repository<Membership>, turnRepository: Repository<Turn>);
    markPayment(turnId: number, amount: number, status: string, userId: number): Promise<Payment>;
    getTurnPayments(turnId: number): Promise<Payment[]>;
}
//# sourceMappingURL=payments.service.d.ts.map