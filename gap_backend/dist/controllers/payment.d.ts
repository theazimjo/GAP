import { Response } from 'express';
import { AuthRequest } from '../middleware/auth';
export declare const markPayment: (req: AuthRequest, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
export declare const getTurnPayments: (req: AuthRequest, res: Response) => Promise<Response<any, Record<string, any>> | undefined>;
//# sourceMappingURL=payment.d.ts.map